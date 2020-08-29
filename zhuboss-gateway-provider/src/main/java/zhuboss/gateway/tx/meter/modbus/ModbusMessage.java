package zhuboss.gateway.tx.meter.modbus;

import zhuboss.gateway.tx.channel.task.DeviceRequestMessage;
import zhuboss.gateway.util.CRC16Util;
import lombok.Data;

@Data
public class ModbusMessage extends DeviceRequestMessage {
	protected byte adr; //0-255
	protected byte funCode; //
	protected byte[] data;

	@Override
	public int hashCode() {
		return (int)adr;
	}

	public byte[] getEncodeBytes() {
		byte[] adu = new byte[data.length + 4];
		adu[0] = adr;
		adu[1] = funCode;
		System.arraycopy(data, 0, adu, 2, data.length);

		//crc16
		System.arraycopy(CRC16Util.calculateCRC(new byte[] {adr,funCode},data, 0,data.length),
				0,adu,data.length+2,2);
		return adu;
	}

	public int getSize() {
		return 2 + data.length;
	}

	public ModbusMessage(byte adr, byte funCode, byte[] data) {
		this.adr = adr;
		this.funCode = funCode;
		this.data = data;
	}

	@Override
	public String getHashAddr() {
	    //同一时间可能向同一表号，发送多个功能码
        String adr = (this.adr&0xff) +"."+(this.funCode&0xff);
		return adr;
	}
}
