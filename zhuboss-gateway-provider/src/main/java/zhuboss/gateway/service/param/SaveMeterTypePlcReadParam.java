package zhuboss.gateway.service.param;

import lombok.Data;
import zhuboss.framework.validate.SysDict;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SaveMeterTypePlcReadParam {

    @NotNull
    private Integer meterKindReadId;
    @NotEmpty
    private String addr;
    
    @NotEmpty
    @SysDict("value_type")
    private String valueType;

    private String ratiovar;
}
