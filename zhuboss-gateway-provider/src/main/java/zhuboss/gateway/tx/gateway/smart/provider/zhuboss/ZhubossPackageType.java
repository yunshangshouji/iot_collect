package zhuboss.gateway.tx.gateway.smart.provider.zhuboss;

public class ZhubossPackageType {
    /**
     * 心跳包
     */
    public static final byte HEART_BEAT = 100;

    /**
     * 注册包
     */
    public static final byte REGISTER = (byte)0xBB;

    /**
     * 下发配置
     */
    public static final byte DOWN_MEA = 102;

    /**
     * 定时上报
     */
    public static final byte DATA_REPORT = 103;

    /**
     * 立刻采集并上报
     */
    public static final  byte COLLECT_REPORT = 113;

    /**
     * 调取5分钟结果统计
     */
    public static final  byte RETRIEVE_STS = 114;
    /**
     * 写命令
     */
    public static final byte WRITE = 104;

    /**
     * 仪表离线
     */
    public static final byte DEV_OFFLINE = 105;

    /**
     * 仪表上线
     */
    public static final byte  DEV_ONLINE = 115;
    /**
     * 读仪表
     */
    public static final byte READ = 106;

    /**
     * 越限告警
     */
    public static final byte OVER_LIMIT_ON = 107;

    /**
     * 越限告警解除
     */
//    public static final byte OVER_LIMIT_OFF = 108;

    /**
     * 遥信变位
     */
    public static final byte SIGNAL = 109;
    /**
     * 错误，连接即将关闭
     */
    public static final byte ERROR = 99;

    public static final byte TIME_STAMP = 98;

    /**
     * 请求连接PLC设备
     */
    public static final byte PLC_CONNECT = 116;

}
