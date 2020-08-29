package zhuboss.gateway.tx.gateway.raw.provider.hongdian.message;


/**
 * 数据包
 * @author Administrator
 *
 */
public class DTU2DscDataMessage {
	private String dtuId;
	private byte[] data;
	
	public DTU2DscDataMessage(String dtuId, byte[] data) {
		this.dtuId = dtuId;
		this.data = data;
	}
	
	public String getDtuId() {
		return dtuId;
	}
	public void setDtuId(String dtuId) {
		this.dtuId = dtuId;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
}
