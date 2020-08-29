package zhuboss.gateway.tx.gateway.raw.provider.simple;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import zhuboss.framework.spring.SpringContextUtils;
import zhuboss.gateway.facade.constants.CollectorTypeEnum;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.service.vo.CachedCollector;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.task.AbstractTask;
import zhuboss.gateway.tx.gateway.IResponseDecoder;
import zhuboss.gateway.tx.gateway.raw.AbstractRawMessageDecoder;

import java.util.List;

@Slf4j
public class ZhubossRawMessageDecoder extends AbstractRawMessageDecoder {
    byte registerBytes[] = new byte[32];
    int registerIdx = 0;
    enum State{
        register,
        data
    }
    State state = State.register;


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() == 0) return;

        if(state == State.register){
            while(registerIdx<32 && in.readableBytes()>0){
                registerBytes[registerIdx] = in.readByte();
                registerIdx++;
            }
            if(registerIdx<32){
                return;
            }
            String gwNo = new String(registerBytes).replaceAll("\0","");
            //注册网关
            CollectorPO collectorPO = SpringContextUtils.getApplicationContext().getBean(CollectorService.class).getCollectorPO(gwNo);
            if(collectorPO!= null){
                ChannelKeys.registerGatewayId(ctx.channel(),gwNo, CollectorTypeEnum.RAW_ZHU,collectorPO.getAppId());
            }else{
                log.warn("网关不存在{}",gwNo);
                ctx.close();
                return;
            }
            //TODO 根据网关编号获取通讯协议
            state = State.data;
            return;
        }

        AbstractTask task = (AbstractTask)ChannelKeys.readAttr(ctx.channel(),ChannelKeys.EXECUTING_TASK);
        if(task == null){
            return;
        }
        IResponseDecoder responseDecoder = task.getResponseDecoder(ctx.channel());
        try {
            if (responseDecoder.hasParsing()) { //已收到部分应答字节
                responseDecoder.readData(in);
                return;
            }
            /**
             * 心跳包判断
             */
            while (in.readableBytes() >= 2 && in.getByte(in.readerIndex()) == 0x7b && in.getByte(in.readerIndex() + 1) == 0x7d) {
                in.readByte();
                in.readByte();
                log.debug("heart beat");
            }
            //继续数据包解析
            responseDecoder.readData(in);
        }catch (Exception e){
            responseDecoder.reset();
        }

    }

    /**
     * 网关中配置心跳包2个字符 '{}",即0x7b 0x7d，但实际会发随机若干个H
     * @param in
     * @return
     */
    private boolean checkHeartBeat(ByteBuf in){
        if(in.readableBytes() <2 ) return false;
        if(in.getByte(in.readerIndex()) == 0x7b && in.getByte(in.readerIndex()+1) == 0x7d){
            in.readByte();
            in.readByte();
            return true;
        }
        return false;
    }

}
