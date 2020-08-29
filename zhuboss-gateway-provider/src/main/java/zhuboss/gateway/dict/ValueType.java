package zhuboss.gateway.dict;

public enum  ValueType {
    UINT8(0,"UINT8"),
    INT8(1,"INT8"),
    UINT16(2,"UINT16"),
    INT16(3,"INT16"),
    IEEE754(4,"IEEE754"),
    UINT32_CDAB(5,"UINT32_CDAB"),
    INT32_CDAB(6,"INT32_CDAB"),
    UINT32_ABCD(7,"UINT32_ABCD"),
    INT32_ABCD(8,"INT32_ABCD"),
    BIT(15,"BIT")
    ;

    private Integer code;
    private String name;
    private ValueType(Integer code,String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
