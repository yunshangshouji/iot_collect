package zhuboss.gateway.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Table("tx_collector")
@FieldNameConstants(asEnum = true)
@Data
public class TxCollectorPO extends AbstractPO {
    @PrimaryKey
    private Integer collectorId;

    private Date lastActiveTime;

    private String ifName;

    private Integer onlineFlag;
    private Date offlineTime;

    private String devErrorMsg;

    private Date devErrorTime;

    private Date lastOnlineTime;

    @ApiModelProperty("应用程序启动时间")
    private Date appStartTime;

    private String devVersion;

    private String appVersion;

    @ApiModelProperty("最后上报时间")
    private Date lastReportTime;

    @ApiModelProperty("最后上报仪表数")
    private Integer lastReportCount;

    @ApiModelProperty("轮询用时")
    private Integer lastLoopSeconds;

}
