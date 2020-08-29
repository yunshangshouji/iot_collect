package zhuboss.gateway.tx.gateway.smart.provider.zhuboss.downvo;

import lombok.Data;

@Data
public class ZhubossProfile {
    private String name;
    /**
     * 报文中的数据位置索引，从0开始
     */
    private Integer addr;

    private String addr2;

    private int valueType;
    /**
     * 变量、常量系数
     */
    private String ratioVar;

    /**
     * 遥信，1、0、null
     */
    private Integer signal;
}
