package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddHisViewColumnParam extends SaveHisViewColumnParam {
    @NotNull
    private Integer viewId;
    @NotNull
    private Integer meterKindReadId;
}
