package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateMeterKindReadParam extends SaveMeterKindReadParam {
    @NotNull
    private Integer id;
}
