package zhuboss.gateway.facade.api.param;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class AlarmConfigParam extends BaseParam {
    @NotEmpty
    @ApiModelProperty("关联ID")
    private String refId;

    @ApiModelProperty("关联站点ID，空默认根站点")
    private String stationRefId;

    @NotNull
    @ApiModelProperty("设备类别ID")
    private Integer meterKindId;

    @NotEmpty
    @ApiModelProperty("预警标题")
    private String title;

    @ApiModelProperty("上限值")
    private BigDecimal fromValue;

    @ApiModelProperty("下限值")
    private BigDecimal toValue;

    @NotEmpty
    @ApiModelProperty("数据项列表")
    private Integer[] meterKindReadId;

    /**
     * 设备列表
     */
    @NotEmpty
    @ApiModelProperty("设备关联ID")
    private String[] meterRefId;

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public Integer getMeterKindId() {
        return meterKindId;
    }

    public void setMeterKindId(Integer meterKindId) {
        this.meterKindId = meterKindId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getFromValue() {
        return fromValue;
    }

    public void setFromValue(BigDecimal fromValue) {
        this.fromValue = fromValue;
    }

    public BigDecimal getToValue() {
        return toValue;
    }

    public void setToValue(BigDecimal toValue) {
        this.toValue = toValue;
    }

    public Integer[] getMeterKindReadId() {
        return meterKindReadId;
    }

    public void setMeterKindReadId(Integer[] meterKindReadId) {
        this.meterKindReadId = meterKindReadId;
    }

    public String[] getMeterRefId() {
        return meterRefId;
    }

    public void setMeterRefId(String[] meterRefId) {
        this.meterRefId = meterRefId;
    }

    public String getStationRefId() {
        return stationRefId;
    }

    public void setStationRefId(String stationRefId) {
        this.stationRefId = stationRefId;
    }
}
