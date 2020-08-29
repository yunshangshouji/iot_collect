package zhuboss.gateway.service.shunzhou;

import zhuboss.gateway.facade.constants.TransTypeEnum;
import zhuboss.gateway.util.JavaUtil;

public class ShunZhouIdUtil {
    /**
     * 返回20位的仪表设备ID
     * @param idParat
     * @return
     */
    public static String buildId(IdParat idParat){
        StringBuffer id = new StringBuffer();
        // 1.传输类型：透传、工业设备
        id.append(idParat.getTransType().getCode());
        //2. com口编号(10进制)
        String idC = "0"+idParat.getInterNo();
        id.append(idC.substring(idC.length()-2));
        //3. 表号(16进制)
        String id2 = "000000000000000000000" + Integer.toHexString(idParat.getDevAddr());
        id2 = id2.substring(id2.length() - 16);
        id.append(id2);
        return id.toString();
    }

    public static IdParat extractId(String id){
        TransTypeEnum transTypeEnum = TransTypeEnum.getByCode(id.substring(0,2));
        Integer interNo = Integer.parseInt(id.substring(2,4));
        Integer devAddr = Integer.parseInt(id.substring(4),16); //16进制
        return new IdParat(transTypeEnum,interNo,devAddr);
    }

}
