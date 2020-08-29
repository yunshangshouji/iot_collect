package zhuboss.gateway.tx.gateway.raw.provider.simple;

import zhuboss.gateway.tx.gateway.raw.provider.hongdian.message.DTUDownMeterMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 透传数据编码
 */
public class SimpleRawMessageEncoder extends MessageToByteEncoder<DTUDownMeterMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, DTUDownMeterMessage msg, ByteBuf out) throws Exception {
        out.writeBytes(msg.getByteBuffer());
        msg.getByteBuffer().release();
    }
}
