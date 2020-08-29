package zhuboss.gateway.adapter.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class Dlt645Var implements Serializable {
    private String name;
    private Integer code;
    private Integer scale;

    public Dlt645Var(String name, Integer code,Integer scale) {
        this.name = name;
        this.code = code;
        this.scale = scale;
    }
}
