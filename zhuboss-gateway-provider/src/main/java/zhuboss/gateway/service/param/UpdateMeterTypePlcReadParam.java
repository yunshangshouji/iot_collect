package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateMeterTypePlcReadParam extends SaveMeterTypePlcReadParam{
    @NotNull
    private Integer id;
}
