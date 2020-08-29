package zhuboss.gateway.tx.gateway.smart.provider.zhuboss;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import zhuboss.framework.spring.SpringContextUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ZhubossUpstreamDecoder extends ByteToMessageDecoder {

    private IDispatcher dispatcher;

    public ZhubossUpstreamDecoder(IDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    enum Part{
        PACK_TYPE,
        LEN,
        DATA
    }
    //
    private static final int MAX_DATA_SIZE = 65535;
    public static final int LEN_SIZE = 2;
    //
    Part nextPart = Part.PACK_TYPE;
    byte packType;
    byte[] payloadlen = new byte[2];
    int dataSize;
    byte[] buffer = new byte[MAX_DATA_SIZE];
    //
    int needSize;

    ChannelHandlerContext ctx;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        this.ctx = ctx;
        parse(buf, out);
    }

    public void parse(ByteBuf buf,List<Object> out) throws IOException, InterruptedException {
        if(buf.readableBytes()<1)	return;
        if(nextPart.equals(Part.PACK_TYPE)){
            packType = buf.readByte();
            nextPart = Part.LEN;
            needSize = 2;
        }else if(nextPart.equals(Part.LEN)){
            int actRead = buf.readableBytes() >= needSize? needSize :buf.readableBytes();
            buf.readBytes(payloadlen, LEN_SIZE - needSize, actRead);
            needSize = needSize - actRead;
            if(needSize == 0){
                nextPart = Part.DATA;
                dataSize = uint16(payloadlen);
                needSize = dataSize;
                if(dataSize == 0){
                    end();
                }
                Assert.isTrue(needSize<65536);

            }
        }else if(nextPart.equals(Part.DATA)){
            int actRead = buf.readableBytes() >= needSize? needSize :buf.readableBytes();
            buf.readBytes(buffer, dataSize - needSize, actRead);
            needSize = needSize - actRead;
            if(needSize == 0){
                end();
            }
        }else {
            log.error("无效Part");
            ctx.writeAndFlush(new ZhubossDataPackage(ZhubossPackageType.ERROR,"invalid Part")).sync();
        }
        parse(buf, out);
    }

    private void end(){
        nextPart = Part.PACK_TYPE;
        needSize = 1;
        byte[] data = Arrays.copyOfRange(buffer,0,dataSize);
        try{
            dispatcher.dispatch(ctx.channel(),new ZhubossDataPackage(packType,data));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            ctx.close();
        }
    }
    public static int uint16(byte[] bytes){
        int num = bytes[0] & 0xff;
        num <<= 8;
        num |=  (bytes[1] & 0xff);
        return num;
    }
}
