package zhuboss.gateway.facade.api.param;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

public class StationParam extends BaseParam {
    @NotEmpty
    @ApiModelProperty("站点名称")
    private String text;

    @NotEmpty
    @ApiModelProperty("关联ID")
    private String refId;

    @ApiModelProperty("上级站点关联ID，空表示根站点")
    private String parentRefId;

    @ApiModelProperty("经度")
    private BigDecimal lng;

    @ApiModelProperty("纬度")
    private BigDecimal lat;

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public String getParentRefId() {
        return parentRefId;
    }

    public void setParentRefId(String parentRefId) {
        this.parentRefId = parentRefId;
    }
}
