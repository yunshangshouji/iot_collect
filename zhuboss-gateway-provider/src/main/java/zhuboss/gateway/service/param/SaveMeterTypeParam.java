package zhuboss.gateway.service.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SaveMeterTypeParam {

    private String typeName;

    private Integer meterKindId;

    private String protocol;

    @ApiModelProperty("波特率")
    private Integer baudRate;

    @ApiModelProperty("校验位")
    private char parity;

    @ApiModelProperty("数据位")
    private Integer byteSize;

    @ApiModelProperty("停止位")
    private Integer stopBits;

    private Integer readMillSeconds;

    private String remark;

    private Byte aliveFlag;
}
