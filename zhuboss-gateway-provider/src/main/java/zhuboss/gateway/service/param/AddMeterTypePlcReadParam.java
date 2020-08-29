package zhuboss.gateway.service.param;

import lombok.Data;
import zhuboss.framework.validate.SysDict;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AddMeterTypePlcReadParam extends SaveMeterTypePlcReadParam{
    @NotNull
    private Integer meterTypeId;
}
