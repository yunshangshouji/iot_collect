package zhuboss.gateway.adapter;

import org.springframework.util.StringUtils;
import zhuboss.framework.exception.BussinessException;
import zhuboss.gateway.po.MeterTypeWritePO;
import zhuboss.gateway.util.JavaUtil;

public class ModbusUtil {

    public static byte[] buildData(int cmd ,String addrHex,String writeUnitsHex,String writeByteSizeHex,String dataHex){
        if(cmd == 0x03 || cmd == 0x04 || cmd == 0x01 || cmd == 0x02 || cmd == 0x05 || cmd == 0x06){
            return JavaUtil.hexStringToBytes(addrHex + dataHex);
        }else if(cmd == 0x0f|| cmd == 0x10){
            return JavaUtil.hexStringToBytes(addrHex + writeUnitsHex + writeByteSizeHex+ dataHex);
        }else{
            throw new RuntimeException("Unsupport "+ cmd);
        }
    }

    /**
     *
     * @param cmd
     * @param addrHex
     * @param writeUnitsHex
     * @param writeByteSizeHex
     * @param dataHex 为空时，表示录入阶段无默认值
     */
    public static void checkWrite(int cmd ,String addrHex,String writeUnitsHex,String writeByteSizeHex,String dataHex){
        if(cmd == 0x05){ //写单线圈
            if(StringUtils.hasText(dataHex) && !dataHex.equalsIgnoreCase("FF00")
                    && !dataHex.equalsIgnoreCase("0000")){
                throw new BussinessException("写单线圈写入值只能为:FF00(On)、0000(Off)");
            }
        }else if(cmd == 0x06){ //写单寄存器
            if(StringUtils.hasText(dataHex) && !dataHex.matches("[0-9|A-F]{4}")){
                throw new BussinessException("写单寄存器写入值只能为4位0-F");
            }
        }else if(cmd == 0x0F){ //写多线圈
            if(StringUtils.hasText(dataHex)){
                int writeByteSize = JavaUtil.hexString2Int(writeByteSizeHex);
                if(dataHex.length() != writeByteSize*2){ //一个字节2hex位
                    throw new BussinessException("写入字节数量不等于"+writeByteSize);
                }
            }

        }else if(cmd == 0x10){ //写多寄存器
            if(StringUtils.hasText(dataHex)){
                int writeByteSize = JavaUtil.hexString2Int(writeByteSizeHex);
                if(dataHex.length() != writeByteSize*2){ //一个字节2hex位
                    throw new BussinessException("写入字节数量不等于"+writeByteSize);
                }
            }
        }else{
            throw new BussinessException("Unsupport");
        }
    }

}
