package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SavePlcDevParam {
    @NotNull
    private Integer collectorId;
    @NotNull
    private Integer meterTypeId;
    @NotEmpty
    private String devName;
    @NotEmpty
    private String addr;
    @NotNull
    private Integer port;
}
