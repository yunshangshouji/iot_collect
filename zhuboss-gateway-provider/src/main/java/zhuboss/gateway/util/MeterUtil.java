package zhuboss.gateway.util;

import org.springframework.util.StringUtils;
import zhuboss.gateway.facade.constants.InterfaceTypeEnum;
import zhuboss.gateway.po.MeterPO;

public class MeterUtil {

    public static String getMeterName(MeterPO meterPO){
        return MeterUtil.getMeterName(meterPO.getDevName(),meterPO.getInterfaceType(),meterPO.getComPort(),meterPO.getAddr(),meterPO.getHost(),meterPO.getPort());
    }

    public static  String getMeterName(String devName,String interfaceType,Integer comPort,Long addr,String ip,Integer port){
        if(StringUtils.hasText(devName)){
            return devName;
        }
        if(interfaceType.equals(InterfaceTypeEnum.COM.getCode())){
            return  "串口"+comPort+":"+ addr;
        }
        if(interfaceType.equals(InterfaceTypeEnum.PLC.getCode())){
            return "PLC:"+ip+":"+port;
        }
        if(interfaceType.equals(InterfaceTypeEnum.TCP.getCode())){
            return "TCP:"+ip+":"+port + ":" + addr;
        }
        return null;
    }

}
