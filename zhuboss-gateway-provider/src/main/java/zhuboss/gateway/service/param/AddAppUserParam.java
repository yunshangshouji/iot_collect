package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AddAppUserParam {
    @NotNull
    private Integer appId;
    @NotEmpty
    private String mail;
    @NotNull
    private Boolean cfgFlag;
    @NotNull
    private Boolean pushFlag;
}
