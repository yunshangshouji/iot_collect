package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserRegisterParam {
    @NotEmpty
    private String mail;
    @NotEmpty
    private String loginPwd;
    private String nickName;


}
