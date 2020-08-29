package zhuboss.gateway.service.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DoAppCheckResult {
    @ApiModelProperty("解除数量")
    private int size=0;

    @ApiModelProperty("解除目录")
    private List<String> targetNames = new ArrayList<>();
}
