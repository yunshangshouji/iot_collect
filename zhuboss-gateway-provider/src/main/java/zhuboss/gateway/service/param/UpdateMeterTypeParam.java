package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateMeterTypeParam extends SaveMeterTypeParam {
    @NotNull
    private Integer id;
}
