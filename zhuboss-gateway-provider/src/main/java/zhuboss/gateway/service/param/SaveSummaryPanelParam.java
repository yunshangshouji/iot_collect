package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SaveSummaryPanelParam {
    @NotNull
    private Integer summaryId;
    @NotNull
    private String title;
}
