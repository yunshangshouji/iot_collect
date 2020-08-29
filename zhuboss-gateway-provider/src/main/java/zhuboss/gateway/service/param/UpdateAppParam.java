package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateAppParam {
    @NotNull
    private Integer appId;
    @NotEmpty
    private String appKey;
    @NotEmpty
    private String appName;
    @NotNull
    private Integer cycleSeconds;
    @NotNull
    private Integer gwLostSeconds;
}
