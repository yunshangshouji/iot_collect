package zhuboss.gateway.service.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class SaveMeterAlarmParam {
    @NotEmpty
    private String title;
    @NotNull
    private Integer meterKindId;
    @NotNull
    private Integer stationId;
    private Integer[] meterId;
    private Integer[] meterKindReadId;

    @ApiModelProperty("预警起始值")
    private BigDecimal fromValue;
    @ApiModelProperty("预警结束值")
    private BigDecimal toValue;

    private String refId;
}
