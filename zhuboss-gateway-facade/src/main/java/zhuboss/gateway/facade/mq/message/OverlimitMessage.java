package zhuboss.gateway.facade.mq.message;

import java.math.BigDecimal;

public class OverlimitMessage extends BaseMessage {
    // 参数
    private String var;
    // 参数名称
    private String varName;
    // 起始值
    private BigDecimal fromValue;
    // 结束值
    private BigDecimal toValue;
    //报警值
    private BigDecimal readValue;

    @Override
    public String getMessageType() {
        return this.getClass().getSimpleName();
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
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

    public BigDecimal getReadValue() {
        return readValue;
    }

    public void setReadValue(BigDecimal readValue) {
        this.readValue = readValue;
    }
}
