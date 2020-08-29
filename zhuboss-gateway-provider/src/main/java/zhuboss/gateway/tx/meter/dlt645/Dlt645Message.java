package zhuboss.gateway.tx.meter.dlt645;

import lombok.Data;
import zhuboss.framework.utils.JavaUtil;
import zhuboss.gateway.dict.ProtocolEnum;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

@Data
public class Dlt645Message {
    final byte[] FE = new byte[]{(byte)0xFE,(byte)0xFE,(byte)0xFE,(byte)0xFE};
//    final int fixLen = head.length + 6 + 1 +1 + 4+4 +1 + 1; // head + 6地址 + 1控制码 +　1长度(8) + 4数据标识 + 4值 +1CS + 1(16H)

    private ProtocolEnum protocolEnum;
    private Long addr;
    private int dataId;
    private BigDecimal value;
    private boolean ok;


    public Dlt645Message(ProtocolEnum protocolEnum, Long addr, int dataId) {
        this.protocolEnum = protocolEnum;
        this.addr = addr;
        this.dataId = dataId;
    }

    /**
     * 响应
     * @param addr
     * @param dataId
     * @param value
     */
    public Dlt645Message(ProtocolEnum protocolEnum, Long addr, int dataId, BigDecimal value, boolean ok) {
        this.protocolEnum = protocolEnum;
        this.addr = addr;
        this.dataId = dataId;
        this.value = value;
        this.ok = ok;
    }

    public byte[] getEncodeBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //帧起始符
        baos.write(Dlt645Constants.START);
        //表地址(补足12位)
        String total = "00000000000"+addr;
        String meterAddrStr = total.substring(total.length() - 12,total.length());
        String last = meterAddrStr.substring(10,12)+meterAddrStr.substring(8,10)+meterAddrStr.substring(6,8)+meterAddrStr.substring(4,6)+meterAddrStr.substring(2,4)+meterAddrStr.substring(0,2);
        byte[] meterAddr = BcdUtil.str2Bcd(last);
        baos.write(meterAddr); //new byte[]{0x12,0x34,0x56,0x78,(byte)0x90,0x12}
        //帧起始符
        baos.write(Dlt645Constants.START);
        //控制码
        baos.write(protocolEnum.equals(ProtocolEnum.DLT2007) ? Dlt645Constants.READ_S_01_2007 : Dlt645Constants.READ_S_01_1997);// 读取数据
        //数据长度
        baos.write(protocolEnum.equals(ProtocolEnum.DLT2007) ? 0x04 : 0x02);//
        //数据(先低位后高位)，注意这里不做倒序，也不加0x33

        byte[] bytes2 = JavaUtil.int2Bytes(dataId);
        //倒序
        int end = protocolEnum.equals(ProtocolEnum.DLT2007) ? -1 : 1; // 2007 4字节, 1997 2字节
        for(int i=3;i> end;i--){
            baos.write(bytes2[i] + 0x33);
        }

        //
        byte[] info = baos.toByteArray();
        //cs 校验码 从帧起始符开始到校验码之前的所有各字节的模 256 的和， 即各字节二进制算术和，不计超过 256 的溢出值。
        int csB = info[0]&0xff;
        for (int i = 1; i < info.length; i++)
        {
            csB += (info[i]&0xff);
//            csB = (byte) (csB & info[i]);
        }
        csB = csB%256;
        //cs 校验码
        baos.write(csB);
        //帧结束符
        baos.write(Dlt645Constants.END);

        ByteArrayOutputStream returnBaos = new ByteArrayOutputStream();
        // 前导字节      在发送帧信息之前，先发送 1-4 个字节 FEH，以唤醒接收方。
        returnBaos.write(FE);
        baos.writeTo(returnBaos);
        return returnBaos.toByteArray();

    }
}
