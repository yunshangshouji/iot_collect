package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserValidateParam {
    @NotEmpty
    private String mail;
    @NotEmpty
    private String verifyCode;


}
