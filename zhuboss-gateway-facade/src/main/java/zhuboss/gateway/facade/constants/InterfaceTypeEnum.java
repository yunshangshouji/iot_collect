package zhuboss.gateway.facade.constants;

public enum  InterfaceTypeEnum {
    COM("com","串口"),
    LORA("lora","LORA"),
    TCP("tcp","TCP"),
    PLC("plc","PLC")
    ;
    private String code;
    private String text;
    InterfaceTypeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }

    public static InterfaceTypeEnum getByCode(String code){
        for(InterfaceTypeEnum interfaceTypeEnum : InterfaceTypeEnum.values()){
            if(interfaceTypeEnum.getCode().equals(code)){
                return interfaceTypeEnum;
            }
        }
        return  null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
