package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SaveHisViewColumnParam  {
    @NotNull
    private Integer width;
}
