package zhuboss.gateway.controller.console.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateDevInfoParam {
    @NotNull
    private Integer id;
    @NotEmpty
    private String devName;
}
