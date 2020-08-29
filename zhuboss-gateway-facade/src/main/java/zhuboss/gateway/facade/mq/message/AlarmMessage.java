package zhuboss.gateway.facade.mq.message;

import java.math.BigDecimal;

public class AlarmMessage {
    private String targetCode;
    private String targetName;
    private BigDecimal val;
    private BigDecimal fromValue;
    private BigDecimal toValue;

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

    public BigDecimal getVal() {
        return val;
    }

    public void setVal(BigDecimal val) {
        this.val = val;
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
}
