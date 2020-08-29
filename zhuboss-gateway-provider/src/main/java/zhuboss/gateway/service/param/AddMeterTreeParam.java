package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AddMeterTreeParam {
    @NotEmpty
    private String text;
    @NotNull
    private Integer pid;
    //如果pid为0,要用stationId
    @NotNull
    private Integer stationId;
    private Integer iconCls;
    private Integer isSubDiagram;
}
