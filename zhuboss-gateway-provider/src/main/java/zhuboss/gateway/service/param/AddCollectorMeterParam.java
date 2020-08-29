package zhuboss.gateway.service.param;

import lombok.Data;
import zhuboss.framework.validate.SysDict;

import javax.validation.constraints.NotEmpty;

@Data
public class AddCollectorMeterParam extends SaveCollectorMeterParam {
    @NotEmpty
    @SysDict("interface_type")
    private String interfaceType;

    private Integer collectorId;

}
