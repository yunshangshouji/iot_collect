package zhuboss.gateway.tx.gateway.smart.provider.zhuboss.downvo;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ZhubossMeta extends DownMessage {
    /**
     * 表类型定义
     */
    private Map<String,ZhubossMeterType> meterTypeMap = new HashMap<>(); //key 为 Integer 非标准json

    /**
     * 表连接与个性化越限配置
     */
    private List<ZhubossMeter> meters = new ArrayList<>();

    /**
     * 上报周期间隔秒
     */
    private Integer cycleSeconds;

    /**
     * LORA网关配置
     * 非LORA网关为NULL
     * 非空时串口全部为1
     */
    private LoraCfg loraCfg;
//    private String test = "xx";
}
