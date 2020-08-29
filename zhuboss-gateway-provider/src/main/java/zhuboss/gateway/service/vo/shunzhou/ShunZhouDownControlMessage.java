package zhuboss.gateway.service.vo.shunzhou;

import lombok.Data;

import java.util.Map;

@Data
public class ShunZhouDownControlMessage {
    private Integer code = 1002;

    private String id;

    private Integer ep = 1;

    private Long serial;

    private Map<String,Boolean> control;
}
