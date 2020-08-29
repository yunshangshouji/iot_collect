package zhuboss.gateway.facade.vo;

import java.io.Serializable;
import java.util.Objects;

public class MeterKindRead implements Serializable {
    private Integer id;
    private String meterKind;
    private String targetCode;
    private String targetName;
    private String unit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMeterKind() {
        return meterKind;
    }

    public void setMeterKind(String meterKind) {
        this.meterKind = meterKind;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, meterKind, targetCode, targetName, unit);
    }
}
