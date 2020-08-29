package zhuboss.gateway.tx.gateway.smart.provider.zhuboss.upvo;

import lombok.Data;

@Data
public class ZhubossRegisterParam {
    /**
     * 设备版本
     */
    private String devVer;

    /**
     * 软件版本
     */
    private String appVer;

    /**
     * 网关编码
     */
    private String devNo;

    /**
     * 密钥
     */
    private String key;

    /**
     * 网卡名称
     */
    private String ifName;

    /**
     * 应用程序启动时间
     */
    private Long appStartTime;
}
