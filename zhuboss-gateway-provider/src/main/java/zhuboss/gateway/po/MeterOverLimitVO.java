package zhuboss.gateway.po;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MeterOverLimitVO {
    private Integer meterId;
    private String targetCode;
    private BigDecimal fromValue;
    private BigDecimal toValue;
}
