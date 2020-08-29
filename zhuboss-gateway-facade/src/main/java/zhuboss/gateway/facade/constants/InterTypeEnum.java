package zhuboss.gateway.facade.constants;

public enum InterTypeEnum {
    com("com","com"),
    eth("eth","eth");

    private String code;
    private String name;

    InterTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


}
