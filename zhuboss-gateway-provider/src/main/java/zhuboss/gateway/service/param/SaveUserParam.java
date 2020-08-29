package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SaveUserParam {
    @NotNull
    private String mail;
    private String mobile;
    private String nickName;
    @NotNull
    private Integer spFlag;
    @NotNull
    private Integer aliveFlag;
}
