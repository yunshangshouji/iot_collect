package zhuboss.gateway.wx.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class BindParam {

    @NotEmpty
    private String mail;

    @NotEmpty
    private String loginPwd;

}
