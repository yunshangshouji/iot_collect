package zhuboss.gateway.tx.gateway.raw.provider.hongdian.message;

/**
 * 注册包
 * @author Administrator
 *
 */
public class DTU2DscRegisterMessage {
	private String dtuId;
	private String localIP;
	private int localPort;
	
	
	
	public DTU2DscRegisterMessage(String dtuId, String localIP, int localPort) {
		this.dtuId = dtuId;
		this.localIP = localIP;
		this.localPort = localPort;
	}
	
	@Override
	public String toString() {
		return "DTU2DscRegisterMessage [dtuId=" + dtuId + ", localIP="
				+ localIP + ", localPort=" + localPort + "]";
	}



	public String getDtuId() {
		return dtuId;
	}
	public void setDtuId(String dtuId) {
		this.dtuId = dtuId;
	}
	public String getLocalIP() {
		return localIP;
	}
	public void setLocalIP(String localIP) {
		this.localIP = localIP;
	}
	public int getLocalPort() {
		return localPort;
	}
	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
	
	
}
