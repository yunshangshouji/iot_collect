package zhuboss.gateway.tx.gateway.smart.provider.zhuboss.upvo;

import lombok.Data;

@Data
public class ZhubossMeterOffline {
    private Integer com;
    private Integer loraAddr;
    private String ip;
    private Integer port;
    private Long addr;
    private String msg;
}
