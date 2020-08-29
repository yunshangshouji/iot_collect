package zhuboss.gateway.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Table("meter_alarm")
@FieldNameConstants(asEnum = true)
public class MeterAlarmPO extends AbstractPO {
    @PrimaryKey
    private Long id;
    private Integer appId;
    private Integer stationId;
    @SubQueryColumn("SELECT full_text FROM station WHERE id = meter_alarm.station_id")
    private String stationName;

    private String refId;

    private String title;

    private Integer meterKindId;

    @ApiModelProperty("预警起始值")
    private BigDecimal fromValue;
    @ApiModelProperty("预警结束值")
    private BigDecimal toValue;

    @ApiModelProperty("越限告警中")
    @SubQueryColumn("(SELECT COUNT(*) FROM alarm_over_limit WHERE meter_alarm_id = meter_alarm.id)")
    private Integer alarmFlag;

    @ApiModelProperty("触发时间")
    private Date thisTime;

    private Long thisMsgId;

    @ApiModelProperty("光字牌确认标识")
    private Integer checkFlag;
    private Integer lastCheckUserId;
    private Date lastCheckTime;

    @ApiModelProperty("显示排序")
    private Integer showOrder;

    private Integer modifier;
    private Date modifyTime;

}
