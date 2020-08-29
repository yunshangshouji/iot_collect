package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateChartParam extends SaveChartParam {
    @NotNull
    private Integer id;
}
