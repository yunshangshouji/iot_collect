package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateHisViewColumnParam extends SaveHisViewColumnParam {
    @NotNull
    private Integer id;
}
