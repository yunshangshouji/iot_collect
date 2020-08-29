package zhuboss.gateway.service.vo;

import lombok.Data;

@Data
public class DevInfoControlProfile {
     private String name;
     private int startAddr;
     private int num;
     private int valueType;
     private int attrType; //#1线圈 2寄存器
    /**
     * 变量、常量系数
     */
    private String ratioVar;
    /**
     * 正常值范围
     */
    private String alarmVal;


     public DevInfoControlProfile(){

    }

    public DevInfoControlProfile(String name, int startAddr, int num, int valueType) {
        this.name = name;
        this.startAddr = startAddr;
        this.num = num;
        this.valueType = valueType;
    }
}
