package zhuboss.gateway.facade.api.param;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class BaseParam implements Serializable {
    @NotNull
    @ApiModelProperty("应用ID")
    private Integer appid;
    @NotEmpty
    @ApiModelProperty("应用密钥")
    private String appkey;

    public Integer getAppid() {
        return appid;
    }

    public void setAppid(Integer appid) {
        this.appid = appid;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }
}
