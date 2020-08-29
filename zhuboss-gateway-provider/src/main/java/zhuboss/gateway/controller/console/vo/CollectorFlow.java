package zhuboss.gateway.controller.console.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 网关流量
 */
@Data
public class CollectorFlow {
    @ApiModelProperty("网关")
    private String devNo;

    private long upperBytes;

    private long downBytes;


    private String upperFlow;

    public CollectorFlow(String devNo, long downBytes, long upperBytes) {
        this.devNo = devNo;
        this.upperBytes = upperBytes;
        this.downBytes = downBytes;
    }

    /**
     * 上行流量
     * @return
     */
    public String getUpperFlow(){
        return convert(this.upperBytes);
    }

    /**
     * 下行流量
     * @return
     */
    public String getDownFlow(){
        return convert(this.downBytes);
    }

    private String convert(long bytes){
        String unit;
        double ratio= 1;
        if(bytes > (1024l*1024l*1024l)){
            unit = "GB";
            ratio = 1024*1024*1024;
        }else if(bytes> (1024*1024) ){
            unit = "MB";
            ratio = 1024*1024;
        }else if(bytes> 1024 ){
            unit = "KB";
            ratio = 1024;
        }else{
            return bytes +"B";
        }
        return new BigDecimal((double)bytes / (ratio)).setScale(2, RoundingMode.HALF_UP).toString()+unit;
    }
}
