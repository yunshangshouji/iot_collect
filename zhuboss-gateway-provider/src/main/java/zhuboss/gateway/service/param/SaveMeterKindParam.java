package zhuboss.gateway.service.param;

import lombok.Data;
import zhuboss.framework.validate.SysDict;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SaveMeterKindParam {
    @NotEmpty
    private String kindCode;
    @NotEmpty
    private String kindName;
    @NotNull
    @SysDict("yn")
    private Integer plcFlag;


}
