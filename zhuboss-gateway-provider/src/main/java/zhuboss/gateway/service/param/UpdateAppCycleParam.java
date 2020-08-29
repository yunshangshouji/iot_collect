package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateAppCycleParam extends SaveAppCycleParam {
    @NotNull
    private Integer id;
}
