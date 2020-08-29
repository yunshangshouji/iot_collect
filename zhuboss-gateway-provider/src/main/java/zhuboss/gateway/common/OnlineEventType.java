package zhuboss.gateway.common;

public enum OnlineEventType {
    ONLINE("1","离线"),
    OFFLINE("2","上线")
    ;

    private String code;
    private String text;
    OnlineEventType(String code, String text){
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
