package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateAppUserParam {
    @NotNull
    private Integer id;
    @NotNull
    private Boolean cfgFlag;
    @NotNull
    private Boolean pushFlag;
}
