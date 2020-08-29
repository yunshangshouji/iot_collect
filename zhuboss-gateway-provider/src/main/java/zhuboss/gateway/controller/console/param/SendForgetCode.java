package zhuboss.gateway.controller.console.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SendForgetCode {
    @NotEmpty
    private String mail;

    private String code;

    private String loginPwd;
}
