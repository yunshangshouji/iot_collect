package zhuboss.gateway.controller.vo;

import lombok.Data;

@Data
public class DataGridField {
    private String field;
    private String title;
    private Integer width;

    public DataGridField(String field, String title, Integer width) {
        this.field = field;
        this.title = title;
        this.width = width;
    }
}
