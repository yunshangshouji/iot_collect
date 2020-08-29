package zhuboss.gateway.service.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddMeterTypeWriteParam extends SaveMeterTypeWriteParam {
    @NotNull
    private Integer meterTypeId;
}
