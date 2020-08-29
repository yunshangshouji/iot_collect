package zhuboss.gateway.spring.mq;

import lombok.Data;

@Data
public class Pause {
    private long id = System.currentTimeMillis();
    private String collectorId;
    private Integer interNo;
    private String terId;
}
