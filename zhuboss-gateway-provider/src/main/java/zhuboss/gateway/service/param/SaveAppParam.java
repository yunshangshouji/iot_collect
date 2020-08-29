package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SaveAppParam {
    //当修改时用
    private Integer appId;

    @NotEmpty
    private String appName;

    private Integer pushFlag;

}
