package zhuboss.gateway.wx.vo;

import lombok.Data;

@Data
public class Jscode2sessionResult {
	private String openid;
	private String session_key;
	private String unionid;
}
