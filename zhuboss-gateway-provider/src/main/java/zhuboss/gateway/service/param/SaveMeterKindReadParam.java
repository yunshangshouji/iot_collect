package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SaveMeterKindReadParam {
    @NotNull
    private Integer meterKindId;

    @NotEmpty
    private String targetCode;

    @NotEmpty
    private String targetName;

    @NotNull
    private Integer signalFlag;

    private Integer scale;

    private String unit;

    private String dlt2007;

}
