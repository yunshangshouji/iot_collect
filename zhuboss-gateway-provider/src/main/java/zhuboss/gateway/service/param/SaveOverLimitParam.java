package zhuboss.gateway.service.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaveOverLimitParam {
    private Integer meterKindReadId;
    private BigDecimal fromValue;
    private BigDecimal toValue;
}
