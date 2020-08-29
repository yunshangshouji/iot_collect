package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateMeterTreeParam {
    @NotNull
    private Integer id;
    @NotEmpty
    private String text;
    private Integer iconCls;
    private Integer isSubDiagram;
}
