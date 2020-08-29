package zhuboss.gateway.service.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SaveMeterTypeWriteParam {
    private Integer meterKindWriteId;
    @NotNull
    private Integer cmd;
    private String addr;
    private String writeUnits;
    private String writeByteSize;
    private String dataHex;
    private String remark;
}
