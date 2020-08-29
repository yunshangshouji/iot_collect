package zhuboss.gateway.controller.console.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PersistConfigParam {
    @NotNull
    private Integer meterKindId;
    @NotNull
    private Integer persistFlag;
    private Integer persistInterval;
    private String persistUnit;
    private Integer persistDays;
}
