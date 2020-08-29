package zhuboss.gateway.tx.gateway.raw.provider.simple;

import zhuboss.framework.spring.SpringContextUtils;
import zhuboss.gateway.facade.constants.CollectorTypeEnum;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.service.vo.CachedCollector;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.task.AbstractTask;
import zhuboss.gateway.tx.gateway.IResponseDecoder;
import zhuboss.gateway.tx.gateway.raw.AbstractRawMessageDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SimpleRawMessageDecoder extends AbstractRawMessageDecoder {
    enum State{
        register,
        data
    }
    State state = State.register;


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() == 0) return;

        if(state == State.register){
                byte[] data = new byte[in.readableBytes()];
            in.readBytes(data);
            String gwNo = new String(data);
            if(gwNo.indexOf('}')>0){
                gwNo = gwNo.substring(0,gwNo.indexOf('}'));

            }else if(gwNo.indexOf(0)>0){
                gwNo = gwNo.substring(0,gwNo.indexOf(0));
            }
            //注册网关
            CachedCollector cachedCollector = SpringContextUtils.getApplicationContext().getBean(CollectorService.class).getCachedCollector(gwNo);
            if(cachedCollector!= null){
                ChannelKeys.registerGatewayId(ctx.channel(),gwNo, CollectorTypeEnum.RAW_REGISTER,cachedCollector.getCollector().getAppId());
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
