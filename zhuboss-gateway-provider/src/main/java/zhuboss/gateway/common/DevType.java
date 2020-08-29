package zhuboss.gateway.common;

public enum DevType {
    COLLECTOR("1","网关"),
    METER("2","设备")
    ;

    private String code;
    private String text;
    DevType(String code, String text){
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
