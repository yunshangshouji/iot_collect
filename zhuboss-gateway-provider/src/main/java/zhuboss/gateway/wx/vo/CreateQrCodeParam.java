package zhuboss.gateway.wx.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CreateQrCodeParam {
    @ApiModelProperty("场景值(字符串)")
    @NotEmpty
    private String sceneStr;
}
