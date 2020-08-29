package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SaveSummaryParam {
    @NotNull
    private String title;
    private Integer refreshInterval;
    private Integer showHeader;
    private Integer showDevName;
}
