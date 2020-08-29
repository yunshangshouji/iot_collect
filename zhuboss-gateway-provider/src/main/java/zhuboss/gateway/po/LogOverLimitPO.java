package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.SubQueryColumn;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("log_over_limit")
@FieldNameConstants(asEnum = true)
public class LogOverLimitPO extends BaseOverLimitPO {
    @SubQueryColumn("SELECT title FROM `meter_alarm` WHERE id= log_over_limit.`meter_alarm_id`")
    private String meterAlarmName;

    private Integer closed;
    private Date closeTime;
}
