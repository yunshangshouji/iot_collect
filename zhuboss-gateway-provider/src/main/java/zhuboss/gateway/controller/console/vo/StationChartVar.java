package zhuboss.gateway.controller.console.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StationChartVar {

    public StationChartVar(String id, String dataName, String bindTargetIdName) {
        this.id = id;
        this.bindTargetIdName = bindTargetIdName;
        this.dataName = dataName;
    }

    private String id;

    @ApiModelProperty("仪表对象名称")
    private String bindTargetIdName;

    @ApiModelProperty("变量中文名")
    private String dataName;

}
