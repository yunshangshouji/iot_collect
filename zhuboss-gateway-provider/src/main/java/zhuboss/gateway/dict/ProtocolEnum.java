package zhuboss.gateway.dict;

public enum ProtocolEnum {
	MODBUS("MODBUS","MODBUS"),
	PLC("PLC","PLC"),

	DLT1997("DLT1997","DLT1997"),
	DLT2007("DLT2007","DLT2007")
	;

	private String code;
	private String name;
	private ProtocolEnum(String code,String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
