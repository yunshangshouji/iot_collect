package zhuboss.gateway.facade.api.param;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;

public class DeleteParam extends BaseParam {
    @NotEmpty
    @ApiModelProperty("关联ID")
    private String refId;

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }
}
