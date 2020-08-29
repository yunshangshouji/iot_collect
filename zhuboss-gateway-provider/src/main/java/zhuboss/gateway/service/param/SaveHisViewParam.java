package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SaveHisViewParam {
    @NotNull
    private Integer meterKindId;
    @NotEmpty
    private String title;
}
