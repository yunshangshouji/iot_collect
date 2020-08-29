package zhuboss.gateway.dict;

public enum CollectorSrcEnum {
    API("1","API"),
    HAND("2","手动")
    ;

    private String code;
    private String name;
    private CollectorSrcEnum(String code,String name) {
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
