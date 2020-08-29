package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddMeterTypeReadParam extends SaveMeterTypeReadParam {
    @NotNull
    private Integer cmd;

    @NotNull
    private Integer meterTypeId;
}
