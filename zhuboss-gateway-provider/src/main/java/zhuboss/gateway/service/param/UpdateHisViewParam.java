package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateHisViewParam extends SaveHisViewParam {
    @NotNull
    private Integer id;
}
