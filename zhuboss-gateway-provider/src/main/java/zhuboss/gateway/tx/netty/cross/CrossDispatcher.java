package zhuboss.gateway.tx.netty.cross;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.MyChannelGroup;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.IDispatcher;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossDataPackage;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossPackageType;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.upvo.ZhubossRegisterParam;

@Component
@Slf4j
public class CrossDispatcher implements IDispatcher {
    @Autowired
    GatewayService gatewayService;

    @Override
    public void dispatch(Channel channel, ZhubossDataPackage zhubossDataPackage) throws Exception {
        if(zhubossDataPackage.getType() == ZhubossPackageType.HEART_BEAT){
            //下发主进程在线状态
            String devNo = ChannelKeys.readAttr(channel, ChannelKeys.COLLECTOR_NO);
            Channel mainChanel = MyChannelGroup.allChannels.findChannelByDevNo(devNo);
            byte data = mainChanel!=null ? (byte)1 : (byte)0 ;
            channel.writeAndFlush(new ZhubossDataPackage(ZhubossPackageType.HEART_BEAT,new byte[]{data})).sync();


        }else if(zhubossDataPackage.getType() == ZhubossPackageType.REGISTER){
            String json = new String(zhubossDataPackage.getData());
            ZhubossRegisterParam zhubossRegisterParam = JSON.parseObject(json, ZhubossRegisterParam.class);
            JsonResponse operateResult = gatewayService.checkRegisterResult(zhubossRegisterParam);
            ZhubossDataPackage ack;
            if(operateResult.getResult() == false){
                ack = new ZhubossDataPackage(ZhubossPackageType.REGISTER,new byte[]{0});
                log.info("注册失败:{}",JSON.toJSONString(zhubossRegisterParam));
            }else{
                //如果已经存在连接，断开连接
                Channel existsChanel = CrossChannelGroup.crossChannels.findChannelByDevNo(zhubossRegisterParam.getDevNo());
                if(existsChanel != null){
                    existsChanel.close();
                }
                //注册应答
                ChannelKeys.setAttr(channel, ChannelKeys.COLLECTOR_NO, zhubossRegisterParam.getDevNo());
                ack = new ZhubossDataPackage(ZhubossPackageType.REGISTER,new byte[]{1});
                log.info("注册成功");
            }
            channel.writeAndFlush(ack).sync();

        }else if(zhubossDataPackage.getType() == ZhubossPackageType.PLC_CONNECT){ // PLC客户端连接失败
            String json = new String(zhubossDataPackage.getData());
            JSONObject jsonObject = JSON.parseObject(json);
            Channel clientChannel = CrossChannelGroup.crossChannels.findChannelByPlcToken(jsonObject.getString("token"));
            if(clientChannel !=null){
                ChannelKeys.setAttr(clientChannel,ChannelKeys.PLC_CONNECT_ERROR,jsonObject.getString("errmsg"));
                synchronized (clientChannel){
                    clientChannel.notifyAll();
                }
            }

        }
    }

}
