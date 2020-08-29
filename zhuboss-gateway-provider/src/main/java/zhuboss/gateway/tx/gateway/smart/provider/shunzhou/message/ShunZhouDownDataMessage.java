package zhuboss.gateway.tx.gateway.smart.provider.shunzhou.message;

import lombok.Data;

@Data
public class ShunZhouDownDataMessage extends AbstractShunZhouDownMessage{
    public ShunZhouDownDataMessage(){

    }

    public ShunZhouDownDataMessage(Object data){
        super.setData(data);
    }
}
