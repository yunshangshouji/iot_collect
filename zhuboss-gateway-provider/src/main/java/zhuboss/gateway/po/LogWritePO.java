package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.SubQueryColumn;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("log_write")
@FieldNameConstants(asEnum = true)
public class LogWritePO extends LogMeter {
    @PrimaryKey
    private Long id;
    @SubQueryColumn("SELECT TEXT FROM station WHERE id = log_write.station_id")
    private String stationName;
    private String taskUuid;
    private String targetCode;
    private String targetName;
    private String gwNo;
    private Long addr;
    private Integer cmd;
    private String dataHex;
    private Integer resCode;
    private String resDataHex;
    private Date createTime;
    private Date respTime;

}
