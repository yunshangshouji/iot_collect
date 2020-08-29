package zhuboss.gateway.tx.meter.modbus;


import lombok.extern.slf4j.Slf4j;

import zhuboss.framework.spring.SpringContextUtils;
import zhuboss.gateway.tx.gateway.IResponseDecoder;
import zhuboss.gateway.util.CRC16Util;
import zhuboss.gateway.util.JavaUtil;

import io.netty.buffer.ByteBuf;

/**
 * 本类的目的是解析一个完整的modbus数据包，交给规则引擎
 * @author Administrator
 *
 */
@Slf4j
public class ModbusResponseDecoder extends IResponseDecoder {
	enum Part{
		ADDR, //1
		FUNCODE, //1
		BYTE_COUNT,
		DATA,
		CRC16,
		ERR_CODE
	}
	
	Part nextPart = Part.ADDR;
	int messageWriteIndex;
	int needSize = 1; //first is ADDR
	byte[] message = new byte[512]; //addr|function|size|data|
	byte[] crc16 = new byte[2];
	int dataIndex; //数据起始位置，抄表3，写入2

	@Override
	public void reset() {
		this.nextPart = Part.ADDR;
	}

	@Override
	public boolean hasParsing() {
		return !nextPart.equals(Part.ADDR);
	}

	@Override
	public void readData(ByteBuf buf) throws Exception{
		if(buf.readableBytes() < 1 ) return;
		
		if(nextPart == Part.ADDR){
			message[0] = buf.readByte();
			nextPart = Part.FUNCODE;
		}else if(nextPart == Part.FUNCODE){
				message[1] = buf.readByte();
				if(message[1] == 0x05 || message[1] == 0x06 ||message[1] == 0x0F){
					nextPart = Part.DATA;
					needSize = 4;
					messageWriteIndex = 2;
				}else if(message[1] == 0x10 ){ //写多个保持寄存器
					nextPart = Part.DATA; // 数据：2起始地址，2寄存器数量
					needSize = 4;
					dataIndex = 2;
					messageWriteIndex = 2;
				}else if(message[1] == 0x01 ||message[1] == 0x02 || message[1] == 0x03 || message[1] == 0x04){ //01,02,03,04,
					nextPart = Part.BYTE_COUNT;
					dataIndex = 3;
				}else if((message[1]&0xff) == 0x83 ){
					//03功能码响应异常
					nextPart = Part.ERR_CODE;
				}else{
					throw new RuntimeException("not support funcode 0x" + JavaUtil.bytesToHexString(new byte[]{message[1]}));
				}
//				needSize = xx ;
		}else if(nextPart == Part.BYTE_COUNT){
			message[2] = buf.readByte();
			nextPart = Part.DATA;
			needSize = message[2] &0xff;
			messageWriteIndex = 3;
			
		}else if(nextPart == Part.DATA){
			int actRead = buf.readableBytes() >= needSize? needSize :buf.readableBytes();
			buf.readBytes(message, messageWriteIndex, actRead);
			messageWriteIndex += actRead;
			needSize = needSize - actRead;
			if(needSize == 0){
				nextPart = Part.CRC16;
				needSize = 2;
			}
			
		}else if(nextPart == Part.CRC16){
			int actRead = buf.readableBytes() >= needSize? needSize :buf.readableBytes();
			buf.readBytes(crc16, 2 - needSize , actRead);
			needSize = needSize - actRead;
			if(needSize == 0){
				byte[] crc = CRC16Util.calculateCRC(null,message,0,messageWriteIndex);
				if(
						(crc[0] == crc16[0] && crc[1] == crc16[1]) // 高低位
						|| (crc[0] == crc16[1] && crc[1] == crc16[0]) //低高位
				){
					byte[] data = new byte[messageWriteIndex - dataIndex];
					System.arraycopy(message, dataIndex, data, 0, data.length);
					if(log.isDebugEnabled()){
						log.debug("adr:{},cmd:{},len:{},data:{}",message[0]&0xff,message[1]&0xff,message[2]&0xff,JavaUtil.bytesToHexString(data, 0, data.length));
					}

					int addr = message[0]&0xff;
					int funCode = (message[1]&0xff);
					SpringContextUtils.getBean(ModbusReceiveReceiveHandler.class).handle(this.getChannel(),new ModbusMessage((byte)addr,(byte)funCode,data));

				}else{
					throw new ModbusParseException("CRC16 check fail,"+"input:" + JavaUtil.bytesToHexString(message, 0, messageWriteIndex)+",read:"+JavaUtil.bytesToHexString(crc16)+",calc:"+JavaUtil.bytesToHexString(crc));
				}
				//归位
				nextPart = Part.ADDR;
			}
			
		}else if(nextPart == Part.ERR_CODE){
			message[2] = buf.readByte();
			messageWriteIndex = 3;
			nextPart = Part.CRC16;
			needSize = 2;
		}
		readData(buf);
	}
	
	public class ModbusParseException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ModbusParseException() {
			super();
		}

		public ModbusParseException(String message, Throwable cause,
				boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public ModbusParseException(String message, Throwable cause) {
			super(message, cause);
		}

		public ModbusParseException(String message) {
			super(message);
		}

		public ModbusParseException(Throwable cause) {
			super(cause);
		}
		
		
	}

}
