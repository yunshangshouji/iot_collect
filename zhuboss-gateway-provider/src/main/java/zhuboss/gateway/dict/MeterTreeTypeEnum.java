package zhuboss.gateway.dict;

public enum  MeterTreeTypeEnum {
    METER("METER","设备"),
    DIR("DIR","目录")
    ;

    private String code;
    private String text;
    private MeterTreeTypeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }
}
