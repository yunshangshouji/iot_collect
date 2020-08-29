package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdatePlcDevParam extends SavePlcDevParam {
    @NotNull
    private Integer id;
}
