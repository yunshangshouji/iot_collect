package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddMeterTypeDltParam{
    @NotNull
    private Integer meterTypeId;
    @NotNull
    private Integer meterKindReadId;
}
