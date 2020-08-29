package zhuboss.gateway.wx.vo;

public class WechatRemoteException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7685882140193749754L;
	private String code;
	private String message;
	
	public WechatRemoteException(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	
}
