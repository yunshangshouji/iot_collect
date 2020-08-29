package zhuboss.gateway.tx.netty.cross.vo;

import lombok.Data;

@Data
public class RegisterResponse {
    private Boolean result;
    private String msg;

    public RegisterResponse(Boolean result, String msg) {
        this.result = result;
        this.msg = msg;
    }
}
