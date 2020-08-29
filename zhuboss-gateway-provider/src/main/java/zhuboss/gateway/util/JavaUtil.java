package zhuboss.gateway.util;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;


public class JavaUtil {
	
	public static final int UUID_BYTE_LENGTH = 16;
	public static final int INT_BYTE_LENGTH = 4;
	
	final static int BUFFER_SIZE = 4096;  
	
	public static String propLoad(String key) throws FileNotFoundException, IOException{
		Properties properties = new Properties();
		properties.load(new FileInputStream("./conf/worker.properties"));
		return properties.getProperty(key);
	}

	public static int hexString2Int(String hex){
		return Integer.parseInt(hex,16);
	}

	/**
	 * 整型转16进制字符串，固定位数，不足补0
	 * @param val
	 * @param length
	 * @return
	 */
	public static  String int2hexString(int val,int length){
		String str = Integer.toHexString(val);
		if(str.length() == length){
			return str;
		}else if(str.length()<length){
			str = "00000000" + str;
		}
		return str.substring(str.length() - length);
	}

	public static String InputStreamTOString(InputStream in, String encoding) throws Exception{
	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
	        byte[] data = new byte[BUFFER_SIZE];  
	        int count = -1;  
	        while((count = in.read(data,0,BUFFER_SIZE)) != -1)  
	            outStream.write(data, 0, count);  
	          
	        data = null;  
	        return new String(outStream.toByteArray(),encoding);  
	    }  

	 public static byte[] InputStreamToBytes(InputStream in) throws Exception{  
         
	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
	        byte[] data = new byte[BUFFER_SIZE];  
	        int count = -1;  
	        while((count = in.read(data,0,BUFFER_SIZE)) != -1)  
	            outStream.write(data, 0, count);  
	          
	        data = null;  
	        return outStream.toByteArray();  
	    }  
	 
	 public static void byteArrayTOFile(byte[] b, String fileName) throws Exception{
			File file = new File(fileName);
			OutputStream os = new FileOutputStream(file);
			os.write(b);
			os.flush();
			os.close();
		 }
	 
	 public static void InputStreamTOFile(InputStream in, String fileName) throws Exception{
		File file = new File(fileName);
		OutputStream os = new FileOutputStream(file);
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
			os.write(data, 0, count);
		in.close();
		os.flush();
		os.close();

	 }

	public static InputStream getURLFileInputStream(String urlPath) {
		try {
			URL url = new URL(urlPath);
			URLConnection conn = url.openConnection();
			InputStream inStream = conn.getInputStream();
			return inStream;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void mkdirs(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	
	public static byte[] uuid2Bytes(UUID uuid){
		long l1 = uuid.getMostSignificantBits();
		long l2 = uuid.getLeastSignificantBits();
		byte[] byteNum = new byte[16];  
		System.arraycopy(long2Bytes(l1), 0, byteNum, 0, 8);
		System.arraycopy(long2Bytes(l2), 0, byteNum, 8, 8);
		return byteNum;
	}
	
	public static UUID bytes2UUID(byte[] bytes){
		return new UUID(bytes2Long(Arrays.copyOfRange(bytes, 0, 8)),
				bytes2Long(Arrays.copyOfRange(bytes, 8,16)));
		
	}
	
	public static byte[] long2Bytes(long num) {  
	    byte[] byteNum = new byte[8];  
	    for (int ix = 0; ix < 8; ++ix) {  
	        int offset = 64 - (ix + 1) * 8;  
	        byteNum[ix] = (byte) ((num >> offset) & 0xff);  
	    }  
	    return byteNum;  
	}  
	
	public static long bytes2Long(byte[] byteNum) {  
	    long num = 0;  
	    for (int ix = 0; ix < 8; ++ix) {  
	        num <<= 8;  
	        num |= (byteNum[ix] & 0xff);  
	    }  
	    return num;  
	} 
	
	public static byte[] int2Bytes(int num) {  
	    byte[] byteNum = new byte[4];  
	    for (int ix = 0; ix < 4; ++ix) {  
	        int offset = 32 - (ix + 1) * 8;  
	        byteNum[ix] = (byte) ((num >> offset) & 0xff);  
	    }  
	    return byteNum;  
	}  
	
	public static int bytes2int(byte[] byteNum) {  
	    int num = 0;  
	    for (int ix = 0; ix < 4; ++ix) {  
	        num <<= 8;  
	        num |= (byteNum[ix] & 0xff);  
	    }  
	    return num;  
	} 
	
	public static String bytesToHexString(byte[] src){ 
		return bytesToHexString(src,0,src.length);
	}
	
	public static String byteBufToHexString(ByteBuf byteBuf){
		StringBuilder stringBuilder = new StringBuilder("");  
		while(byteBuf.readableBytes()>0){
			 int v = byteBuf.readByte() & 0xFF;  
		        String hv = Integer.toHexString(v);  
		        if (hv.length() < 2) {  
		            stringBuilder.append(0);  
		        }  
		        stringBuilder.append(hv);  
		}
		return stringBuilder.toString();
	}
			
	public static String bytesToHexString(byte[] src,int start,int endExclusive){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = start; i < endExclusive; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	}  
	public static byte charToByte(char c) {  
	    return (byte) "0123456789ABCDEF".indexOf(c);  
	}  
	
	public static String hexStringToTrim(String text) {
		StringBuilder sb = new StringBuilder();
		char[] c2 = new char[]{0,0};
		int c = 0;
		for(int i=0;i<text.length();i++){
			if(
					(text.charAt(i)>='0' && text.charAt(i)<='9')
					||(text.charAt(i)>='A' && text.charAt(i)<='F')
					||(text.charAt(i)>='a' && text.charAt(i)<='f')){
				if(c==0 ){
					c2[0] = text.charAt(i);
					c=1;
				}else if (c==1){
					c2[1] = text.charAt(i);
					sb.append(c2[0]);
					sb.append(c2[1]);
					c=0;
				}
			}
		}
		return sb.toString();
	}
	/** 
	 * Convert hex string to byte[] 
	 * @param hexString the hex string 
	 * @return byte[] 
	 */  
	public static byte[] hexStringToBytes(String hexString) {  
	    if (hexString == null || hexString.equals("")) {  
	        return null;  
	    }
	    if(hexString.length()%2>0){ //一个字节两位16进制数
			hexString = "0"+hexString;
		}
	    hexString = hexString.toUpperCase();  
	    int length = hexString.length() / 2;
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];  
	    for (int i = 0; i < length; i++) {  
	        int pos = i * 2;  
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	    return d;  
	}  
	
	public static byte[] gzip(byte[] src) throws IOException{

    	ByteArrayOutputStream os = new ByteArrayOutputStream();
    	GZIPOutputStream gos = new GZIPOutputStream(os);
    	gos.write(src);
    	gos.finish();
    	gos.flush();
    	gos.close();
    	return os.toByteArray();
    
	}
	
	public static byte[] gunzip(byte[] src) {
    	//还原
		try{
    	ByteArrayInputStream is = new ByteArrayInputStream(src);
    	 GZIPInputStream gis = new GZIPInputStream(is); 
    	 return JavaUtil.InputStreamToBytes(gis);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	//压缩直接数组
	public static byte[] compress(byte[] data) {
		byte[] output = new byte[0];
		Deflater compresser = new Deflater();
		compresser.reset();
		compresser.setInput(data);
		compresser.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
		try {
			byte[] buf = new byte[1024];
			while (!compresser.finished()) {
				int i = compresser.deflate(buf);
				bos.write(buf, 0, i);
			}
			output = bos.toByteArray();
		} catch (Exception e) {
			output = data;
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		compresser.end();
		return output;
	}

	//解压缩 字节数组
	public static byte[] decompress(byte[] data) {
		byte[] output = new byte[0];

		Inflater decompresser = new Inflater();
		decompresser.reset();
		decompresser.setInput(data);

		ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
		try {
			byte[] buf = new byte[1024];
			while (!decompresser.finished()) {
				int i = decompresser.inflate(buf);
				o.write(buf, 0, i);
			}
			output = o.toByteArray();
		} catch (Exception e) {
			output = data;
			e.printStackTrace();
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		decompresser.end();
		return output;
	}

	/**
	 *
	 * @param bytes 长度为4字节
	 * @return
	 */
	public static float bytes2float(byte[] bytes){
		int i = JavaUtil.bytes2int(bytes);
		return  Float.intBitsToFloat(i);
	}

	/**
	 *
	 * @param value
	 * @return 4个字节
	 */
	public static byte[] float2bytes(float value){
		int x =Float.floatToRawIntBits(value);
		byte[] bs = JavaUtil.int2Bytes(x);
		return bs;
	}


	public static void main(String[] args){
		System.out.println(JavaUtil.hexStringToBytes("1").length);
//		byte[] b= new byte[3];
//		b[0] = 1;
//		b[1] = 2;
//		b[2] =3 ;
//		byte[] n = Arrays.copyOfRange(b, 0, 2);
		
//		UUID uuid = UUID.randomUUID();
//		System.out.println(uuid);
//		System.out.println(uuid2Bytes(uuid));
//		System.out.println(bytes2UUID(uuid2Bytes(uuid)));
		
//		System.out.println(bytes2int(int2Bytes(0)));
		
//		String s = "abc1231313131300000";
//		System.out.println(new String(hexStringToBytes(bytesToHexString(s.getBytes()))));
		
		byte[] b = JavaUtil.uuid2Bytes(UUID.randomUUID());
		System.out.println(UUID.nameUUIDFromBytes(b));
		System.out.println(JavaUtil.bytes2UUID(b));


	}
}
