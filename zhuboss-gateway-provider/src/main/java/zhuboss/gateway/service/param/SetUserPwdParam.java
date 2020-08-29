package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SetUserPwdParam {
    @NotNull
    private Integer userId;
    @NotEmpty
    private String loginPwd;
}
