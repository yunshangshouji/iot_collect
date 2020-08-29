package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateUserVarParam extends SaveUserVarParam {
    @NotNull
    private Integer id;
}
