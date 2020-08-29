package zhuboss.gateway.facade.vo;

public class SignalItem extends Item {
    private Integer id;

    public SignalItem(){

    }

    public SignalItem(String value, String text) {
        super(value, text);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
