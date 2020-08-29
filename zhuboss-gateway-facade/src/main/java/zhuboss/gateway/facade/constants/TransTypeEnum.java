package zhuboss.gateway.facade.constants;

public enum TransTypeEnum {

    JSON("03","JSON"),
    RAW("04","透传");

    private String code;
    private String name;

    TransTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static TransTypeEnum getByCode(String code){
        for(TransTypeEnum transTypeEnum : TransTypeEnum.values()){
            if(transTypeEnum.getCode().equals(code)){
                return transTypeEnum;
            }
        }
        return  null;
    }

}
