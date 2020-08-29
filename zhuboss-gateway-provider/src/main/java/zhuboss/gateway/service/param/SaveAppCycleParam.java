package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SaveAppCycleParam {
    private Integer stationId;
    private Integer meterKindId;
    private Integer meterTypeId;
    @NotNull
    private Integer cycleSeconds;
    private String remark;
}
