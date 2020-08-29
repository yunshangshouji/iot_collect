package zhuboss.gateway.facade.mq.message;

import java.math.BigDecimal;

public class SignalMessage extends BaseMessage{
    private String targetCode;
    private String targetName;
    private BigDecimal readValue;

    @Override
    public String getMessageType() {
        return null;
    }
}
