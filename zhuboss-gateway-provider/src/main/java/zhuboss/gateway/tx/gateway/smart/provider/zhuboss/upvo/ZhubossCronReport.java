package zhuboss.gateway.tx.gateway.smart.provider.zhuboss.upvo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class ZhubossCronReport {
    /**
     * 串口
     */
    private Integer com;

    private Integer loraAddr;


    private String ip;

    private Integer port;
    
    /**
     * 地址
     */
    private Long addr;


    /**
     * 秒级时间戳
     */
    private Long ts;

    /**
     * 状态不正常时errorMsg非空
     */
    private String errorMsg;

    /**
     * 状态正常时values非空
     */
    private Map<String,Object> values;

    public Date getReadTimeDate(){
        if(ts == null){
            return null;
        }
        return new Date(ts*1000l);
    }

}
