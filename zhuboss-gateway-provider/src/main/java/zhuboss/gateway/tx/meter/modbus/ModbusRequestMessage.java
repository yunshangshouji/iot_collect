package zhuboss.gateway.tx.meter.modbus;

import zhuboss.gateway.util.CRC16Util;
import lombok.Data;

@Data
public class ModbusRequestMessage extends ModbusMessage {
	private Integer meterType;

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
	
	public ModbusRequestMessage(byte adr, byte funCode, byte[] raedCommand) {
		super(adr,funCode,raedCommand);
		this.meterType = meterType;
	}
}
