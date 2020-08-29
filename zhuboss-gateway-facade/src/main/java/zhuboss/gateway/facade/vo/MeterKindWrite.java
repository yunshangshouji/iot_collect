package zhuboss.gateway.facade.vo;

import java.io.Serializable;
import java.util.Objects;

public class MeterKindWrite implements Serializable {
    private Integer id;
    private String meterKind;
    private String targetCode;
    private String targetName;

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

    @Override
    public int hashCode() {
        return Objects.hash(id, meterKind, targetCode, targetName);
    }
}
