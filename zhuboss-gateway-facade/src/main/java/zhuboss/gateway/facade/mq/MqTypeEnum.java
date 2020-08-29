package zhuboss.gateway.facade.mq;

public enum MqTypeEnum {
    /**
     * 遥测
     */
    read,

    /**
     * 越限
     */
    alarm,

    /**
     * 遥信
     */
    signal,

    /**
     * 网关离线
     */
    collector_offline,

    /**
     * 仪表离线
     */
    meter_offline,

    /**
     * 仪表上线
     */
    meter_online

}
