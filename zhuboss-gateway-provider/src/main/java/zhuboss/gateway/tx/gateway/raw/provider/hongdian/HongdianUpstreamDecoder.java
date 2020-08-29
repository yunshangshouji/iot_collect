package zhuboss.gateway.tx.gateway.raw.provider.hongdian;

import zhuboss.gateway.console.websocket.WebSocketSessionFactory;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.gateway.raw.provider.hongdian.message.DTU2DscCloseMessage;
import zhuboss.gateway.tx.gateway.raw.provider.hongdian.message.DTU2DscDataMessage;
import zhuboss.gateway.tx.gateway.raw.provider.hongdian.message.DTU2DscRegisterMessage;
import zhuboss.gateway.util.JavaUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class HongdianUpstreamDecoder extends ByteToMessageDecoder{
	static final Logger debugTcpLogger = LoggerFactory.getLogger(">>TCP");
	static final Logger logger = LoggerFactory.getLogger(HongdianUpstreamDecoder.class);
	/**
	 * 头尾结束标志
	 */
	private static final byte START_END_BYTE = 0x7B;
	
	/**
	 * 终端请求注册
	 */
	private static final byte PACKAGE_TYPE_DTU_REGISTER = 0x01;
	/**
	 * 终端请求注销
	 */
	private static final byte PACKAGE_TYPE_DTU_CLOSE = 0x02;
	/**
	 * 无效命令或协议包
	 */
	private static final byte PACKAGE_TYPE_DTU_NOUSE = 0x04;
	/**
	 * 接收到dsc用户数据的应答包
	 */
	private static final byte PACKAGE_TYPE_DTU_RCV_ACK = 0x05;
	
	/**
	 * 发送给dsc的用户数据包
	 */
	private static final byte PACKAGE_TYPE_DTU2DSC = 0x09;
	
	private static final int LEN_SIZE = 2;
	private static final int LOCALIPPORT_LEN = 6;
	private static final int DTU_ID_LEN = 11;
	private static final int MAX_DATA_SIZE = 65535;
	public static final int UN_BODY_LENGTH = 1+1+2+11+1; // = 总长度-数据体长度
	enum Part{
		START, //1,0x7b
		PCAKGE_TYPE, //1
		DATA_LEN, //2,
		DTU_ID, //11,
		DATA,
		LOCAL_IP_PORT, //6
		END //1
	}
	
	byte packageType;
	byte[] len = new byte[LEN_SIZE];
	byte[] localIpPort = new byte[LOCALIPPORT_LEN];
	byte[] dtuId = new byte[DTU_ID_LEN];
	byte[] data = new byte[MAX_DATA_SIZE];

	Part nextPart = Part.START;
	int needSize = 0;
	int dataSize;
	
	ChannelHandlerContext ctx;
	Boolean debugTcp;
	public HongdianUpstreamDecoder(Boolean debugTcp){
		this.debugTcp = debugTcp;
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf,
			List<Object> out) throws Exception {
		/**
		 * 打印TCP日志
		 */
		if(debugTcp){
			debugTcpLogger.info(">>"+JavaUtil.byteBufToHexString(buf.duplicate())); //貌似release是针对原始ByteBuf的
		}
		
		/**
		 * 支持在线WebSocket查看TCP通信
		 */
		try {
			String dutId = ChannelKeys.readAttr(ctx.channel(), ChannelKeys.COLLECTOR_NO);
			if(dutId !=null) {
				List<Session> sessionList = WebSocketSessionFactory.getInstance().getSessionMap().get(dutId);
				if(sessionList!=null) {
					Iterator<Session> iterator = sessionList.iterator();
					while(iterator.hasNext()) {
						Session session = iterator.next();
						if(session.isOpen()) {
							session.getBasicRemote().sendText(WebSocketSessionFactory.getDateTime() +">>"+JavaUtil.byteBufToHexString(buf.duplicate()));
						}else {
							iterator.remove();
						}
					}
				}
			}
		}catch(Exception e) {
			logger.warn("Websocket send fail!" + e.getMessage());
		}
		//
		this.ctx = ctx;
		parse(buf, out);
		
	}

	public void parse(ByteBuf buf,List<Object> out) throws IOException{
		if(buf.readableBytes()<1)	return;
		if(nextPart.equals(Part.START)){
			byte b = buf.readByte();
			Assert.isTrue(b == START_END_BYTE);
			nextPart = Part.PCAKGE_TYPE;
		}else if(nextPart.equals(Part.PCAKGE_TYPE)){
			packageType = buf.readByte();
			Assert.isTrue(packageType == PACKAGE_TYPE_DTU_REGISTER
					||packageType == PACKAGE_TYPE_DTU_CLOSE
					|| packageType == PACKAGE_TYPE_DTU_NOUSE
					|| packageType == PACKAGE_TYPE_DTU_RCV_ACK
					|| packageType == PACKAGE_TYPE_DTU2DSC);
			nextPart = Part.DATA_LEN;
			needSize = LEN_SIZE;
		}else if(nextPart.equals(Part.DATA_LEN)){
			int actRead = buf.readableBytes() >= needSize? needSize :buf.readableBytes();
			buf.readBytes(len, LEN_SIZE - needSize, actRead);
			needSize = needSize - actRead;
			if(needSize == 0){
				nextPart = Part.DTU_ID;
				needSize = DTU_ID_LEN;
				dataSize = uint16(len) - UN_BODY_LENGTH;
				Assert.isTrue(dataSize<MAX_DATA_SIZE);
			}
		}else if(nextPart.equals(Part.DTU_ID)){
			int actRead = buf.readableBytes() >= needSize? needSize :buf.readableBytes();
			buf.readBytes(dtuId, DTU_ID_LEN - needSize, actRead);
			needSize = needSize - actRead;
			if(needSize == 0){
				if(this.packageType == PACKAGE_TYPE_DTU_REGISTER){
					nextPart = Part.LOCAL_IP_PORT;
					needSize = 6;
				}else if(this.packageType == PACKAGE_TYPE_DTU_CLOSE || this.packageType == PACKAGE_TYPE_DTU_NOUSE|| this.packageType ==PACKAGE_TYPE_DTU_RCV_ACK){
					nextPart = Part.END;
				}else if(this.packageType == PACKAGE_TYPE_DTU2DSC){
					nextPart = Part.DATA;
					needSize = dataSize;
				}
			}
		}else if(nextPart.equals(Part.DATA)){
			int actRead = buf.readableBytes() >= needSize? needSize :buf.readableBytes();
			buf.readBytes(data, dataSize - needSize, actRead);
			needSize = needSize - actRead;
			if(needSize == 0){
				nextPart = Part.END;
			}
		}else if(nextPart.equals(Part.LOCAL_IP_PORT)){
			int actRead = buf.readableBytes() >= needSize? needSize :buf.readableBytes();
			buf.readBytes(localIpPort, LOCALIPPORT_LEN - needSize, actRead);
			needSize = needSize - actRead;
			if(needSize == 0){
				nextPart = Part.END;
			}
		}else if(nextPart.equals(Part.END)){
			Assert.isTrue(buf.readByte() == START_END_BYTE);
			//trigger message
			if(packageType == PACKAGE_TYPE_DTU2DSC){
				byte[] dtuData = new byte[dataSize];
				System.arraycopy(data,0, dtuData, 0, dataSize);
				out.add(new DTU2DscDataMessage(getDtuId(dtuId),dtuData));
			}else if(packageType == PACKAGE_TYPE_DTU_REGISTER){
				out.add(new DTU2DscRegisterMessage(getDtuId(dtuId), bytesToIp(this.localIpPort), uint16(new byte[]{this.localIpPort[4],this.localIpPort[5]})));
			}else if(packageType == PACKAGE_TYPE_DTU_CLOSE){
				out.add(new DTU2DscCloseMessage(getDtuId(dtuId)));
			}else  if(packageType == PACKAGE_TYPE_DTU_NOUSE){ //无用心跳包
				//
			}
			nextPart = Part.START;
		}
		parse(buf, out);
	}
	
	public static String getDtuId(byte[] dtuId){
		return new String(dtuId);
	}
	public static String bytesToIp(byte[] src) {
        return (src[0] & 0xff) + "." + (src[1] & 0xff) + "." + (src[2] & 0xff)
                + "." + (src[3] & 0xff);
    }
	public static int uint16(byte[] bytes){
		int num = bytes[0] & 0xff;
		num <<= 8;
		num |=  (bytes[1] & 0xff);
		return num;
	}
	
	void myAssert(boolean b){
		if (!b) {
			ctx.channel().close();
		}
	}
	
}
