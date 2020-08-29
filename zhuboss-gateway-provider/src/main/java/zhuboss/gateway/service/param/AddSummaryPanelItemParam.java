package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddSummaryPanelItemParam {
    @NotNull
    private Integer summaryPanelId;
    @NotNull
    private Integer meterId;
    @NotNull
    private Integer meterKindReadId;
}
