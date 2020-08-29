package zhuboss.gateway.tx.gateway.raw.provider.hongdian;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zhuboss.gateway.tx.gateway.raw.provider.hongdian.message.Dsc2DTURegisterAckMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class HongdianRegisterAckMessageEncoder extends MessageToByteEncoder<Dsc2DTURegisterAckMessage>{
	static final Logger logger = LoggerFactory.getLogger(HongdianRegisterAckMessageEncoder.class);
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Dsc2DTURegisterAckMessage msg, ByteBuf out) throws Exception {
		//回写应答
		DTUUtil.writeResponse(out, (byte)0x81, msg.getDtuId());
		ctx.channel().flush();
		logger.debug("DTUDownRegisterAckMessage responsed" );
	}

}
