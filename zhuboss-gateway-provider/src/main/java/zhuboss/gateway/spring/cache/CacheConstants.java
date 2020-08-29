package zhuboss.gateway.spring.cache;

public enum CacheConstants {
    PERSIST_OPTIONS(CacheConstants.persist_options,"历史存储选项",60),

    COLLECTORS(CacheConstants.collectors,"登录用户名称",60),

    METER_TYPE(CacheConstants.meter_type,"仪表配置元数据",60), //读&写

    SIGNAL_FLAG(CacheConstants.signal_flag,"遥信名称",30),

    CycleSeconds(CacheConstants.cycle_seconds,"轮询间隔",60),

    GW_LOST_SECONDS(CacheConstants.gw_lost_seconds,"网关离线不活时间",60),

    PLC_DEV_PO(CacheConstants.plc_dev_po,"PLC DevPO",60), //用于连接验证缓存

    APP(CacheConstants.app,"APP",60),

    DICT(CacheConstants.dict,"字典",120);

    private String value;
    private String text;
    /**
     * 过期时间（秒）
     */
    private Integer expires;


    CacheConstants(String value, String text, Integer expires) {
        this.value = value;
        this.text = text;
        this.expires = expires;
    }

    /**
     *
     */
    public static final String persist_options = "persist_options";

    public static final String app = "app";

    /**
     * 仪表元数据缓存
     */
    public static final String meter_type = "meter_type";

    /**
     * 网关仪表配置缓存
     */
    public static final String collectors = "collectors";

    public static final String gw_lost_seconds = "gw_lost_seconds";

    public static final String plc_dev_po = "plc_dev_po";

    public static final String signal_flag = "signal_flag";
    public static final String cycle_seconds = "cycle_seconds";
    public static final String dict = "dict";

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public Integer getExpires() {
        return expires;
    }


}
