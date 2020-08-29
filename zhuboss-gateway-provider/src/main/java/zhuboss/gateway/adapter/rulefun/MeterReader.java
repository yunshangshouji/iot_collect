package zhuboss.gateway.adapter.rulefun;

import java.math.BigDecimal;

import zhuboss.gateway.dict.ProtocolEnum;

import zhuboss.gateway.util.JavaUtil;


/**
 * 注意寄存器一个字是两个字节！
 * @author Administrator
 *
 */
public class MeterReader {
	private int startAddr;
	private int len;
	private byte[] data;

	/**
	 * 线圈、输入状态的位解析
	 * @param pos
	 * @return
	 */
	public long BIT(int pos){
		int cIndex = Math.floorDiv(pos,8);
		byte c = data[cIndex];
		int bitOffset = pos%8;
		int val =  (c >> (7-bitOffset))&0x1;
		return val;
	}
	/**
	 * 无符号16整数
	 * @param startWord
	 * @return
	 */
	public long UINT16(int startWord){
		int dataPos = startWord - startAddr;
		byte[] numBytes = new byte[2];
		numBytes[0] = data[dataPos*2];
		numBytes[1] = data[dataPos*2+1];
		return RunFun.BigEndianUnsigned(numBytes);
	}

	/**
	 * 有符号16位整数
	 * @param startWord
	 * @return
	 */
	public long INT16(int startWord){
		int dataPos = startWord - startAddr;
		byte[] numBytes = new byte[2];
		numBytes[0] = data[dataPos*2];
		numBytes[1] = data[dataPos*2+1];
		return RunFun.BigEndianSigned(numBytes);
	}

	/**
	 * 无符号32位整数（高低位）
	 * @param startWord
	 * @return
	 */
	public long UINT32_ABCD(int startWord){
		int dataPos = startWord - startAddr;
		byte[] numBytes = new byte[4];
		numBytes[0] = data[dataPos*2];
		numBytes[1] = data[dataPos*2 + 1];
		numBytes[2] = data[dataPos*2 + 2];
		numBytes[3] = data[dataPos*2 + 3];
		return RunFun.BigEndianUnsigned(numBytes);
	}

	/**
	 * 有符号32位整数(高低位)
	 * @param startWord
	 * @return
	 */
	public long INT32_ABCD(int startWord){
		int dataPos = startWord - startAddr;
		byte[] numBytes = new byte[4];
		numBytes[0] = data[dataPos*2];
		numBytes[1] = data[dataPos*2 + 1];
		numBytes[2] = data[dataPos*2 + 2];
		numBytes[3] = data[dataPos*2 + 3];
		return RunFun.BigEndianSigned(numBytes);
	}


	/**
	 * 无符号32位整数(低高位)
	 * @param startWord
	 * @return
	 */
	public long UINT32_CDAB(int startWord){
		int dataPos = startWord - startAddr;
		byte[] numBytes = new byte[4];
		numBytes[0] = data[dataPos*2 + 2];
		numBytes[1] = data[dataPos*2 + 3];
		numBytes[2] = data[dataPos*2];
		numBytes[3] = data[dataPos*2 + 1];
		return RunFun.BigEndianUnsigned(numBytes);
	}

	/**
	 * 有符号32位整数(低高位)
	 * @param startWord
	 * @return
	 */
	public long INT32_CDAB(int startWord){
		int dataPos = startWord - startAddr;
		byte[] numBytes = new byte[4];
		numBytes[0] = data[dataPos*2 + 2];
		numBytes[1] = data[dataPos*2 + 3];
		numBytes[2] = data[dataPos*2];
		numBytes[3] = data[dataPos*2 + 1];
		return RunFun.BigEndianSigned(numBytes);
	}

	/**
	 * 32位float
	 * @param startWord
	 * @return
	 */
	public float IEEE754(int startWord){
		int dataPos = startWord - startAddr;
		byte[] numBytes = new byte[4]; //float 32固定 4字节
		numBytes[0] = data[dataPos*2];
		numBytes[1] = data[dataPos*2+1];
		numBytes[2] = data[dataPos*2+2];
		numBytes[3] = data[dataPos*2+3];
		return JavaUtil.bytes2float(numBytes);
	}

	/**
	 * xml中的字符串参数转换为真实类型
	 * @param funName
	 * @param args
	 * @return
	 */
	public static Object[] paramConvert(ProtocolEnum protocol,String funName,String args){
		if(protocol.equals(ProtocolEnum.MODBUS)){
			Integer startWord = Integer.parseUnsignedInt(args,16);
			return new Object[]{startWord};
		}else if(protocol.equals(ProtocolEnum.DLT1997)){
			byte[] bytes= JavaUtil.hexStringToBytes(args);
			Integer funCode = JavaUtil.bytes2int(new byte[]{0,0,bytes[0],bytes[1]});
			return  new Object[]{funCode};
		}else{
			throw new RuntimeException("Not support " + protocol);
		}
	}

	public int getStartAddr() {
		return startAddr;
	}

	public void setStartAddr(int startAddr) {
		this.startAddr = startAddr;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public static void main(String[] args){
		System.out.println(new BigDecimal(3*0.003));
	}
}
