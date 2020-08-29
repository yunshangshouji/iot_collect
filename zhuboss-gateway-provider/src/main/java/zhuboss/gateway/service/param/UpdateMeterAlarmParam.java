package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateMeterAlarmParam extends SaveMeterAlarmParam {
    @NotNull
    private  Long id;
}
