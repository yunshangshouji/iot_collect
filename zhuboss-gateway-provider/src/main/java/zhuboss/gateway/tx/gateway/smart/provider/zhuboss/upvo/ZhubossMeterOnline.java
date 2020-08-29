package zhuboss.gateway.tx.gateway.smart.provider.zhuboss.upvo;

import lombok.Data;

import java.util.Map;

@Data
public class ZhubossMeterOnline {
    private Integer com;
    private Integer loraAddr;
    private String ip;
    private Integer port;
    private Long addr;
    private Map<String, Object> values;
}
