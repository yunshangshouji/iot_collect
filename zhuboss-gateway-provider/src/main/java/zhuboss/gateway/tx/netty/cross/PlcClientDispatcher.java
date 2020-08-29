package zhuboss.gateway.tx.netty.cross;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.spring.SpringContextUtils;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.service.MeterService;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossDataPackage;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossPackageType;
import zhuboss.gateway.tx.netty.cross.vo.RegisterRequest;
import zhuboss.gateway.tx.netty.cross.vo.RegisterResponse;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

@Slf4j
public class PlcClientDispatcher extends ByteToMessageDecoder {
    @Autowired
    MeterService meterService;

    enum State{
        register,
        data
    }
    State state = State.register;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(state.equals(State.register)){
            byte[] buf = new byte[in.readableBytes()];
            in.readBytes(buf,0,in.readableBytes());
            baos.write(buf);
            if(buf[buf.length-1] != 0){ //末尾结束符
                return;
            }
            String registerJSON = new String(baos.toByteArray(),0,baos.size()-1,"UTF-8");
            RegisterRequest registerParam = JSONObject.parseObject(registerJSON, RegisterRequest.class);
            RegisterResponse registerResponse = register(ctx.channel(),registerParam);
            ByteBuf responseByteBuf = PooledByteBufAllocator.DEFAULT.buffer();
            responseByteBuf.writeBytes(JSON.toJSONString(registerResponse).getBytes(Charset.forName("UTF-8")));
            ctx.writeAndFlush(responseByteBuf).sync();
            if(registerResponse.getResult() == false){
                //通道建立失败，要关闭连接，避免客户端异步续操
                ChannelKeys.setAttr(ctx.channel(),ChannelKeys.PLC_CONNECT_TOKEN,null);
                ctx.close();
                return;
            }else{
                state = State.data;
            }
        }
        if(in.readableBytes()==0){
            return;
        }
        //转发模式
        Channel plcGatewayChannel = ChannelKeys.readAttr(ctx.channel(),ChannelKeys.PLC_CHANNEL);
        Assert.isTrue(plcGatewayChannel != null);
        if(!plcGatewayChannel.isActive()){
            ctx.close();
            return;
        }
        //转发
        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer(1); //        ByteBuf writeByteBuf = Unpooled.wrappedBuffer(in);
        compositeByteBuf.addComponent(true,in);
        in.retain();
        plcGatewayChannel.writeAndFlush(compositeByteBuf);
        in.readerIndex(in.readableBytes());

       /* ByteBuf newWriteBuf = PooledByteBufAllocator.DEFAULT.buffer(in.readableBytes()); //必须初始大小，默认512，溢出将丢弃
        in.readBytes(newWriteBuf);
        plcGatewayChannel.writeAndFlush(newWriteBuf).sync();*/
    }


    private RegisterResponse register(Channel clientChannel,RegisterRequest registerParam) throws InterruptedException {
        log.info("设备连接注册{}",JSON.toJSONString(registerParam));
        String errMsg;
        /**
         * 身份验证
         */
        ConnInfo connInfo = null;
        try{
            if(registerParam.getDevId() != null){ //普通用户
                MeterPO meterPO = SpringContextUtils.getBean(MeterService.class).selectCacheableValidateByDevNO(registerParam.getUserName(),registerParam.getLoginPwd(),registerParam.getDevId());
                connInfo = new ConnInfo();
                connInfo.setGwNo(meterPO.getDevNo());
                connInfo.setAddr(meterPO.getHost());
                connInfo.setPort(meterPO.getPort());
            }else{
                connInfo = new ConnInfo();
                connInfo.setGwNo(registerParam.getGwNo());
                connInfo.setAddr(registerParam.getAddr());
                connInfo.setPort(registerParam.getPort());
            }
        }catch (BussinessException e){
            log.error(e.getMessage());
            return new RegisterResponse(false,e.getMessage());
        }
        if(connInfo == null ){
            errMsg = "设备"+registerParam.getDevId()+"不存在或无法访问";
            log.error(errMsg);
            return new RegisterResponse(false,errMsg);
        }
        //验证通过,创建与PLC通信隧道
        Channel gatewayChannel = CrossChannelGroup.crossChannels.findChannelByDevNo(connInfo.getGwNo());
        if(gatewayChannel == null){
            errMsg = "网关不在线";
            log.error(errMsg);
            return new RegisterResponse(false,errMsg);
        }
        log.info("发起与网关握手...");
        String token = UUID.randomUUID().toString().replaceAll("\\-",""); //固定32位UUID
        String plcConnect = connInfo.getAddr()+"," + connInfo.getPort()+"," + token ; //plc地址 + plc端口 + 回连token
        log.info("连接参数:"+plcConnect);
        ChannelKeys.setAttr(clientChannel,ChannelKeys.PLC_CONNECT_TOKEN,token);
        gatewayChannel.writeAndFlush(new ZhubossDataPackage(ZhubossPackageType.PLC_CONNECT,plcConnect.getBytes())).sync();

        synchronized (clientChannel){
            clientChannel.wait(20*1000); // 最大等待时间20秒
        }
        Channel plcChannel = ChannelKeys.readAttr(clientChannel,ChannelKeys.PLC_CHANNEL);
        if(plcChannel == null){
            errMsg = ChannelKeys.readAttr(clientChannel,ChannelKeys.PLC_CONNECT_ERROR) ;
            if(errMsg == null){
                errMsg = "连接设备超时或设备不在线";
            }
            log.error(errMsg);
            return new RegisterResponse(false,errMsg);
        }
        log.info("握手成功");
        state = State.data;//进入数据转发模式
        return new RegisterResponse(true,"登录成功");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        /**
         * 销毁网关端的连接
         */
        Channel plcGatewayChannel = ChannelKeys.readAttr(ctx.channel(),ChannelKeys.PLC_CHANNEL);
        if(plcGatewayChannel != null){
            plcGatewayChannel.close();
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(),cause);
        super.exceptionCaught(ctx, cause);
    }
}
