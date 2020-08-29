package zhuboss.gateway.tx.meter;

import io.netty.buffer.ByteBuf;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.task.AbstractTask;
import zhuboss.gateway.tx.channel.task.DebugTask;
import zhuboss.gateway.tx.gateway.IResponseDecoder;


public class DebugResponseDecoder extends IResponseDecoder {

    @Override
    public void readData(ByteBuf buf) throws Exception {
        //TODO 打印
        AbstractTask abstractTask = (AbstractTask) ChannelKeys.readAttr(this.getChannel(), ChannelKeys.EXECUTING_TASK);
        if(abstractTask instanceof DebugTask){
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            ((DebugTask)abstractTask).setResponseData(data);

            //结束任务
            ChannelKeys.setAttr(this.getChannel(),ChannelKeys.EXECUTING_TASK,null);
            synchronized (abstractTask){
                abstractTask.notifyAll();
            }
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public boolean hasParsing() {
        // TODO 这个调试用途，极低的出错概率不考虑
        return false;
    }
}
