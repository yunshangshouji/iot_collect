package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SaveAppCfgParam {
    @NotEmpty
    private String appKey;
    @NotNull
    private Integer cycleSeconds;
    @NotNull
    private Integer gwLostSeconds;

}
