package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateMeterTypeReadParam extends SaveMeterTypeReadParam {
    @NotNull
    private Integer id;
}
