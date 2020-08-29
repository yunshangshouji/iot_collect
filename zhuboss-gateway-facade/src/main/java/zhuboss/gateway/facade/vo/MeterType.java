package zhuboss.gateway.facade.vo;

import java.io.Serializable;
import java.util.Objects;

public class MeterType implements Serializable {
    private Integer id;
    private String meterKind;
    private String typeName;

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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, meterKind, typeName);
    }
}
