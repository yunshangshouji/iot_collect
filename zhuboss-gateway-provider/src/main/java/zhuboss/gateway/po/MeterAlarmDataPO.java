package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("meter_alarm_data")
@FieldNameConstants(asEnum = true)
public class MeterAlarmDataPO extends AbstractPO {
    @PrimaryKey
    private Integer id;
    private Long meterAlarmId;
    private Integer meterKindReadId;
    private Date createTime;
}
