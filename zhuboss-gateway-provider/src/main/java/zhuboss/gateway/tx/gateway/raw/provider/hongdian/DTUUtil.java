package zhuboss.gateway.tx.gateway.raw.provider.hongdian;

import zhuboss.gateway.util.JavaUtil;

import io.netty.buffer.ByteBuf;

public class DTUUtil {
	public static void writeResponse(ByteBuf byteBuf, byte type,String dtuId,byte... data) {
		//回写应答
				byteBuf.writeByte(0x7b); //start flag
				byteBuf.writeByte(type); //type
				byte[] lenBytes = JavaUtil.int2Bytes(HongdianUpstreamDecoder.UN_BODY_LENGTH + (data!=null?data.length:0));
				byteBuf.writeByte(lenBytes[2]); //length
				byteBuf.writeByte(lenBytes[3]); //length
				byteBuf.writeBytes(dtuId.getBytes());
				if(data != null) {
					byteBuf.writeBytes(data); 
				}
				byteBuf.writeByte(0x7b); //end flag
	}
}
