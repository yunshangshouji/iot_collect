package zhuboss.gateway.service.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SaveMeterTypeReadTargetParam {

    @NotNull
    private Integer meterKindReadId;

    @NotEmpty
    private String valueType;

    @NotEmpty
    private String addrHex;

    private String ratiovar;

}
