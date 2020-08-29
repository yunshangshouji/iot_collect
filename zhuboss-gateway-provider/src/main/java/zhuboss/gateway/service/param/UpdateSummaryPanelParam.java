package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateSummaryPanelParam extends SaveSummaryPanelParam {
    @NotNull
    private Integer id;
}
