package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class SaveStationParam {
    @NotEmpty
    private String text;

    private String refId;

    private BigDecimal lng;

    private BigDecimal lat;

    @NotNull
    private Integer pid;
}
