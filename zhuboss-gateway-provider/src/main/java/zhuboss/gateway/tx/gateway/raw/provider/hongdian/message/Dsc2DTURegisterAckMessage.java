package zhuboss.gateway.tx.gateway.raw.provider.hongdian.message;

public class Dsc2DTURegisterAckMessage {
	private String dtuId;

	public Dsc2DTURegisterAckMessage(String dtuId) {
		this.dtuId = dtuId;
	}

	public String getDtuId() {
		return dtuId;
	}

	public void setDtuId(String dtuId) {
		this.dtuId = dtuId;
	}
}
