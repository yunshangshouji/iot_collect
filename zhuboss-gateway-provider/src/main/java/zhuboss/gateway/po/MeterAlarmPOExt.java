package zhuboss.gateway.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MeterAlarmPOExt extends MeterAlarmPO {
    @ApiModelProperty("用于数据绑定编辑")
    private List<Integer> meterId;

    @ApiModelProperty("用于数据绑定编辑")
    private List<Integer> meterKindReadId;


    @ApiModelProperty("设备对象名称")
    private String meterNames;

    @ApiModelProperty("变量")
    private String targetCodes;
    @ApiModelProperty("变量中文名")
    private String targetNames;

}
