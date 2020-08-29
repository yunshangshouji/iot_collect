package zhuboss.gateway.tx.gateway.raw.provider.hongdian.message;

/**
 * 注销包
 * @author Administrator
 *
 */
public class DTU2DscCloseMessage {
	private String dtuId;
	
	
	public DTU2DscCloseMessage(String dtuId) {
		this.dtuId = dtuId;
	}
	public String getDtuId() {
		return dtuId;
	}
	public void setDtuId(String dtuId) {
		this.dtuId = dtuId;
	}
	
}
