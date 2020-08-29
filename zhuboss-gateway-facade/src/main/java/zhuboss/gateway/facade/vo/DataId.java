package zhuboss.gateway.facade.vo;

public class DataId extends Item {
    private Integer id;

    private String unit;

    private String meterKind;

    public DataId(){

    }

    public DataId(Integer id, String unit,String meterKind,String value, String text) {
        super(value, text);
        this.id = id;
        this.unit = unit;
        this.meterKind = meterKind;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMeterKind() {
        return meterKind;
    }

    public void setMeterKind(String meterKind) {
        this.meterKind = meterKind;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
