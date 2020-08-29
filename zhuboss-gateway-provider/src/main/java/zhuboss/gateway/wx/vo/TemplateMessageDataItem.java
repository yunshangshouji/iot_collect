package zhuboss.gateway.wx.vo;

import lombok.Data;

@Data
public class TemplateMessageDataItem {
    private String value;
    private String color;

    public TemplateMessageDataItem(String value, String color) {
        this.value = value;
        this.color = color;
    }
}
