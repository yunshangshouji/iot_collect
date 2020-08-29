package zhuboss.gateway.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;

import java.util.Date;

@Data
@FieldNameConstants(asEnum = true)
public class LogMeter extends AbstractPO {
    private Integer appId;
    private Integer stationId;
    private Integer meterKindId;
    @ApiModelProperty("设备ID")
    private Integer meterId;
    @ApiModelProperty("监测对象名称")
    private String meterName;
    private Date happenTime;
    private Date createTime;
}
