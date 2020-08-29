package zhuboss.gateway.controller.console.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ChangePidParam {
    @NotNull
    private Integer sourceId;
    @NotNull
    private Integer targetId;

    /**
     * 'append'、'top' 或 'bottom'
     */
    @NotEmpty
    private String point;

}
