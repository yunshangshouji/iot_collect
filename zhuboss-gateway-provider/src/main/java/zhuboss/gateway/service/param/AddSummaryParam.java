package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddSummaryParam extends SaveSummaryParam {
    @NotNull
    private Integer summaryId;
}
