package zhuboss.gateway.tx.meter.dlt645;

public class Dlt645Constants {
    public static final byte FE = (byte)0xfe;
    /**
     * 帧起始符
     */
    public static final byte START = 0x68;

    /**
     * 帧结束符
     */
    public static final byte END = 0x16;

    /**
     * 请求读数据
     */
    public static final byte READ_S_01_2007 = (byte)0x11;

    public static final byte READ_S_01_1997 = (byte)0x01;


}
