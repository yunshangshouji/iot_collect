package zhuboss.gateway.facade.constants;

public enum CollectorTypeEnum {
    ZHUBOSS("1","智能"),
    RAW_REGISTER("2","透传(注册包)"),
    RAW_HONGDIAN("3","透传(宏电)"),
    RAW_ZHU("4","透传(\\0+32)")
    ;

    private String code;
    private String text;
    CollectorTypeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }


    public static boolean isRAW(String code){
        CollectorTypeEnum collectorTypeEnum = getByCode(code);
        return  collectorTypeEnum.name().startsWith("RAW_");
    }

    public static boolean isRAW(CollectorTypeEnum collectorTypeEnum){
        return  collectorTypeEnum.name().startsWith("RAW_");
    }

    public static CollectorTypeEnum getByCode(String code){
        for(CollectorTypeEnum collectorTypeEnum : CollectorTypeEnum.values()){
            if(collectorTypeEnum.getCode().equals(code)){
                return collectorTypeEnum;
            }
        }
        return  null;
    }

}
