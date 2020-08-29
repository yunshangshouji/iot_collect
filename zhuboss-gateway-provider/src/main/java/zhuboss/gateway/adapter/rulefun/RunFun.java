package zhuboss.gateway.adapter.rulefun;

import java.math.BigInteger;


/**
 * BigEndian高字节存储在起始地址（java),LittleEndian低高字节存储在起始地址<br>
 * Unsigned：无符号; signed正负数，首位0正数1负数
 * @author dell
 *
 */
public class RunFun {
	
	public static long readNum(byte[] data,boolean bigEndian,boolean unsigned) {
		//负数在前面补F
		boolean negFlag = !unsigned && (data[bigEndian ? 0 : (data.length-1)]>>7 == 0xffffffff); //是否为负数
			long num = negFlag ? 0xffffffffffffffffL : 0; //这是int32的ff
		    for (int i = 0; i < data.length; ++i) {  
		    	int idx = bigEndian ? i : (data.length - 1 - i);
		        num <<= 8;  
		        num |= (data[idx] &0xff); 
		    }  
			return num;  
		}
		
		public static long BigEndianUnsigned(byte... data) {
			return readNum(data,true,true);
		}
		public static long BigEndianSigned(byte... data) {
			return readNum(data,true,false);
		}
		public static long LittleEndianUnsigned(byte... data) {
			return readNum(data,false,true);
		}
		public static long LittleEndianSigned(byte... data) {
			return readNum(data,false,false);
		}
	
	public static void main(String[] args) {
		System.out.println(0xf481);
		System.out.println(BigEndianSigned(new byte[] {(byte)0xff,(byte)0x9B}));
		
		Long n =  0x10l;
		System.out.println(n&0xff);
		n = n<<8;
		System.out.println(n);
		n = n<<8;
		System.out.println(n);
		n = n<<8;
		System.out.println(n);
		n = n<<8;
		System.out.println(Long.toHexString(n));
	}
}
