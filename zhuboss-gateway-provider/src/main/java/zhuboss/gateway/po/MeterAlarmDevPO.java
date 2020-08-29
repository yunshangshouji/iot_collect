package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("meter_alarm_dev")
@FieldNameConstants(asEnum = true)
public class MeterAlarmDevPO extends AbstractPO {
    @PrimaryKey
    private Integer id;
    private Long meterAlarmId;
    private Integer meterId;
    private Date createTime;
}
