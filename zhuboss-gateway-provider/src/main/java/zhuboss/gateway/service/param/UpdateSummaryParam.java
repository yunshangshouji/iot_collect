package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateSummaryParam extends SaveSummaryParam {
    @NotNull
    private Integer id;
}
