package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;

import java.math.BigDecimal;

@Data
@Table("log_signal")
@FieldNameConstants(asEnum = true)
public class LogSignalPO extends LogMeter {
    @PrimaryKey
    private String id;
    private String targetCode;
    private String targetName;
    private BigDecimal readValue;

}
