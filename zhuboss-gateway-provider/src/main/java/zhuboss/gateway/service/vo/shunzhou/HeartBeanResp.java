package zhuboss.gateway.service.vo.shunzhou;

import lombok.Data;

@Data
public class HeartBeanResp {
    private int code=1001;
    private int result =0;
    private long timestamp = System.currentTimeMillis()/1000;
}
