package zhuboss.gateway.tx.gateway.smart.provider.zhuboss;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import zhuboss.gateway.util.JavaUtil;

public class ZhubossDataPackageMessageEncoder extends MessageToByteEncoder<ZhubossDataPackage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ZhubossDataPackage msg, ByteBuf out) throws Exception {
        out.writeByte(msg.getType());
        byte[] lenBytes = JavaUtil.int2Bytes(msg.getData().length);
        out.writeByte(lenBytes[2]);
        out.writeByte(lenBytes[3]);
        out.writeBytes(msg.getData());
    }

}
