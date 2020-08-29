package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ModifyUserPwd {
    @NotEmpty
    private String oldLoginPwd;
    @NotEmpty
    private String newLoginPwd;
}
