package zhuboss.gateway.facade.vo;


import java.io.Serializable;

public class Item implements Serializable {
    private String value;
    private String text;
    public Item(){

    }
    public Item(String value, String text) {
        this.value = value;
        this.text = text;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
