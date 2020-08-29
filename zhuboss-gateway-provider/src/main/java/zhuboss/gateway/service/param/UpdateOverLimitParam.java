package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateOverLimitParam extends SaveOverLimitParam {
    @NotNull
    private Integer id;
}
