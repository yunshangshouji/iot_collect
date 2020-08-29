package zhuboss.gateway.tx.gateway.raw.provider.hongdian.message;

import io.netty.buffer.PooledByteBufAllocator;

import io.netty.buffer.ByteBuf;

/**
 * 向485表设备发送的消息
 */
public class DTUDownMeterMessage {

    ByteBuf byteBuffer;

    public DTUDownMeterMessage(byte[] data){
        byteBuffer = PooledByteBufAllocator.DEFAULT.buffer(data.length);
        byteBuffer.writeBytes(data);
    }

    public ByteBuf getByteBuffer() {
        return byteBuffer;
    }

}
