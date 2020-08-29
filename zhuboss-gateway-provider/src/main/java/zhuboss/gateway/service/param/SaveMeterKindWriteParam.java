package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SaveMeterKindWriteParam {
    @NotNull
    private Integer meterKindId;

    @NotEmpty
    private String targetCode;

    @NotEmpty
    private String targetName;

    private Integer showOrder;

    private Integer alarmBit;
    /**
     * 是否支持遥控
     */
    private Integer controlFlag;

}
