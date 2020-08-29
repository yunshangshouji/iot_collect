package zhuboss.gateway.tx.gateway.smart.provider.zhuboss.downvo;

import com.google.common.collect.ListMultimap;
import lombok.Data;

@Data
public class ZhubossMeter {
    /**
     * 来自网关的枚举定义：  0:COM, 1:LORA, 2:TCP 3:PLC
     */
    private Integer type;

    /**
     * 串口
     */
    private Integer comPort;

    /**
     * lora网关
     */
    private Integer loraAddr;

    /**
     * 注意基于TCP的连接，必须基于ip&port排序
     */
    private String ip;
    private Integer port;

    /**
     * 表号
     */
    private Long addr;

    private Integer meterTypeId;

    private ListMultimap<String,String>  overlimit;

}
