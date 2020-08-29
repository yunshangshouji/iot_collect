package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.*;

import java.util.Date;

@Data
@Table("log_lost")
@FieldNameConstants(asEnum = true)
public class LogLostPO extends LogMeter {
    @PrimaryKey
    private String id;
    @SubQueryColumn("SELECT TEXT FROM station WHERE id = log_lost.station_id")
    private String stationName;
    private String eventType;
    private String devType;
}
