package zhuboss.gateway.wx.vo;

import lombok.Data;

@Data
public class GetAccessKeyResult {
    private String access_token;
    private int expires_in;
    private Integer errcode;
    private String errmsg;
}
