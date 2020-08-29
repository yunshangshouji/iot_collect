package zhuboss.gateway.tx.gateway.smart.provider.shunzhou;

import com.alibaba.fastjson.JSON;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.gateway.smart.provider.shunzhou.message.AbstractShunZhouDownMessage;
import zhuboss.gateway.tx.gateway.smart.provider.shunzhou.message.ShunZhouDownDataMessage;
import zhuboss.gateway.tx.gateway.smart.provider.shunzhou.message.ShunZhouDownRegisterMessage;
import zhuboss.gateway.util.CRC16Util;
import zhuboss.gateway.util.JavaUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import zhuboss.framework.spring.SpringContextUtils;

public class ShunZhouDownMessageEncoder extends MessageToByteEncoder<AbstractShunZhouDownMessage> {
    private final byte[] magic = new byte[]{(byte)0xAA,(byte)0x55};
    private final byte[] version_enctype_type_reserved = new byte[]{(byte)4,(byte)0,1,0};
    private final byte[] version_enctype_type_reserved_register = new byte[]{(byte)4,(byte)0,2,0};

    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractShunZhouDownMessage msg, ByteBuf out) throws Exception {
        /**
         * Bytes: 2	2	1	1	1	1	2
         * magic	payloadlen	version	enctype	type	reserved	crc
         */
        String text = JSON.toJSONString(msg.getData());
        byte[] dataBytes = text.getBytes(ShunZhouUpstreamDecoder.ShunZhouEncoding);
        out.writeBytes(magic);
        byte[] lenBytes = JavaUtil.int2Bytes(dataBytes.length);
        out.writeByte(lenBytes[2]);
        out.writeByte(lenBytes[3]);
        if(msg instanceof ShunZhouDownDataMessage){
            out.writeBytes(version_enctype_type_reserved);
        }else if(msg instanceof ShunZhouDownRegisterMessage){
            out.writeBytes(version_enctype_type_reserved_register);
        }
        byte[] crc16 = CRC16Util.calculateCRC(null,dataBytes,0,dataBytes.length);
        out.writeBytes(crc16);
        //payload
        out.writeBytes(dataBytes);


    }

}
