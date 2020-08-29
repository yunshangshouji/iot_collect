package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SaveCollectorParam {
    @NotEmpty
    private String devNo;

    private String devName;

    @NotNull
    private Integer stationId;

    private String refId;

    @NotEmpty
    private String collectorType;
    @NotEmpty
    private String secretKey;

    private Integer reportPeriod;

}
