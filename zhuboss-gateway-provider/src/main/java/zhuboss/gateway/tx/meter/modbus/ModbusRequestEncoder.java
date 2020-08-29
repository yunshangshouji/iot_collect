package zhuboss.gateway.tx.meter.modbus;

import zhuboss.gateway.tx.gateway.raw.provider.hongdian.message.DTUDownMeterMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.util.JavaUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ModbusRequestEncoder extends MessageToByteEncoder<ModbusMessage>{

	static final Logger logger = LoggerFactory.getLogger(ModbusRequestEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, ModbusMessage msg, ByteBuf byteBuf) throws Exception {
		String dtuId = ChannelKeys.readAttr(ctx.channel(), ChannelKeys.COLLECTOR_NO);
		try {
			logger.debug("向DTU{}下发：{}",dtuId,JavaUtil.bytesToHexString(msg.getEncodeBytes()));
			//回写应答03
			ctx.channel().writeAndFlush(new DTUDownMeterMessage(msg.getEncodeBytes()));
//			DTUUtil.writeResponse(byteBuf, (byte)0x89, dtuId,msg.getEncodeBytes());
			ctx.channel().flush();}
		catch(Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(cause.getMessage(),cause);
		ctx.channel().close();
		super.exceptionCaught(ctx, cause);
	}

}
