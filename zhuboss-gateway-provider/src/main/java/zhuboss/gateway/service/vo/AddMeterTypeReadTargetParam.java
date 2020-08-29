package zhuboss.gateway.service.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddMeterTypeReadTargetParam extends SaveMeterTypeReadTargetParam {
    @NotNull
    private Integer readId;

}
