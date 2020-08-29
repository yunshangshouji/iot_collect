package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.SubQueryColumn;
import zhuboss.framework.mybatis.mapper.Table;

@Data
@Table("alarm_over_limit")
@FieldNameConstants(asEnum = true)
public class AlarmOverLimitPO extends BaseOverLimitPO {
    @SubQueryColumn("select full_text from station where id = alarm_over_limit.station_id")
    private String stationName;
}
