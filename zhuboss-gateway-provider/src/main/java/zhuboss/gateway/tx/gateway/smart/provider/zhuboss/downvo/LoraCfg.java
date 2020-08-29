package zhuboss.gateway.tx.gateway.smart.provider.zhuboss.downvo;

import lombok.Data;

@Data
public class LoraCfg {
    /**
     * 信道
     */
    private Integer loraChan;

    /**
     * 速率
     */
    private Integer loraSped;

    /**
     * 传输模式
     */
    private Integer loraTransMode;
}
