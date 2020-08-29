package zhuboss.gateway.tx.gateway.smart.provider.shunzhou;

import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.gateway.smart.provider.shunzhou.message.ShunZhouUpperDataMessage;
import zhuboss.gateway.tx.gateway.smart.provider.shunzhou.message.ShunZhouUpperRegisterMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.util.Assert;
import zhuboss.framework.spring.SpringContextUtils;

import java.io.IOException;
import java.util.List;

public class ShunZhouUpstreamDecoder extends ByteToMessageDecoder {
    enum Part{
        MAGIC, //2,0xAA55
        PAYLOADLEN, //2
        VERSION, //1,
        ENCTYPE, //1,
        TYPE, //1
        RESERVED, //6
        CRC, //1
        DATA
    }
    public static final String ShunZhouEncoding = "UTF-8";
    private static final int MAGIC_SIZE = 2;
    private static final int PAYLOADLEN_SIZE = 2;
    private static final int CRC_SIZE = 2;
    private static final int MAX_DATA_SIZE = 65535;
    byte[] magic = new byte[MAGIC_SIZE];
    byte[] payloadlen = new byte[PAYLOADLEN_SIZE];
    int dataSize;
    byte version;
    byte enctype;
    byte type;
    byte reserved;
    byte[] crc = new byte[CRC_SIZE];
    byte[] data = new byte[MAX_DATA_SIZE];
    Part nextPart = Part.MAGIC;
    int needSize = MAGIC_SIZE;


    ChannelHandlerContext ctx;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        this.ctx = ctx;
        parse(buf, out);
    }

    public void parse(ByteBuf buf,List<Object> out) throws IOException {
        if(buf.readableBytes()<1)	return;
        if(nextPart.equals(Part.MAGIC)){
            int actRead = buf.readableBytes() >= needSize? needSize :buf.readableBytes();
            buf.readBytes(magic, MAGIC_SIZE - needSize, actRead);
            needSize = needSize - actRead;
            if(needSize == 0){
                nextPart = Part.PAYLOADLEN;
                needSize = PAYLOADLEN_SIZE;
            }
        }else if(nextPart.equals(Part.PAYLOADLEN)){
            int actRead = buf.readableBytes() >= needSize? needSize :buf.readableBytes();
            buf.readBytes(payloadlen, PAYLOADLEN_SIZE - needSize, actRead);
            needSize = needSize - actRead;
            if(needSize == 0){
                nextPart = Part.VERSION;
                dataSize = uint16(payloadlen);
                Assert.isTrue(dataSize<65536);
            }
        }else if(nextPart.equals(Part.VERSION)){
            version = buf.readByte();
            Assert.isTrue(version == 4);
            nextPart = Part.ENCTYPE;
        }else if(nextPart.equals(Part.ENCTYPE)){
            enctype = buf.readByte();
            Assert.isTrue(enctype == 0 || enctype == 1 || enctype == 2);
            nextPart = Part.TYPE;
        }else if(nextPart.equals(Part.TYPE)){
            type = buf.readByte();
            Assert.isTrue(type == 1 || type == 2);
            nextPart = Part.RESERVED;
        }else if(nextPart.equals(Part.RESERVED)){
            reserved = buf.readByte();
            nextPart = Part.CRC;
            needSize = CRC_SIZE;
        }else if(nextPart.equals(Part.CRC)){
            int actRead = buf.readableBytes() >= needSize? needSize :buf.readableBytes();
            buf.readBytes(crc, CRC_SIZE - needSize, actRead);
            needSize = needSize - actRead;
            if(needSize == 0){
                nextPart = Part.DATA;
                needSize = dataSize;
                if(dataSize == 0){ //这是一个心跳包
                    //什么都不做，重新下一个包
                    nextPart = Part.MAGIC;
                    needSize = MAGIC_SIZE;
                }
            }
        }else if(nextPart.equals(Part.DATA)){
            int actRead = buf.readableBytes() >= needSize? needSize :buf.readableBytes();
            buf.readBytes(data, dataSize - needSize, actRead);
            needSize = needSize - actRead;
            if(needSize == 0){
                String json = new String(data,0,dataSize,ShunZhouEncoding);
                if(type ==1){ //数据包
                    ShunZhouUpperDataMessage shunZhouUpperDataMessage = new ShunZhouUpperDataMessage();
                    shunZhouUpperDataMessage.setJson(json);
                    out.add(shunZhouUpperDataMessage);
                }else if(type == 2){ //注册包
                    ShunZhouUpperRegisterMessage shunZhouUpperRegisterMessage = new ShunZhouUpperRegisterMessage();
                    shunZhouUpperRegisterMessage.setJson(json);
                    out.add(shunZhouUpperRegisterMessage);
                }
                nextPart = Part.MAGIC;
                needSize = MAGIC_SIZE;
            }
        }
        parse(buf, out);
    }

    public static int uint16(byte[] bytes){
        int num = bytes[0] & 0xff;
        num <<= 8;
        num |=  (bytes[1] & 0xff);
        return num;
    }
}
