package zhuboss.gateway.service.vo;

import lombok.Data;

@Data
public class HisDataQueryCondition {
    private String var;
    private String compare;
    private Object value;

    public HisDataQueryCondition(String var, String compare, Object value) {
        this.var = var;
        this.compare = compare;
        this.value = value;
    }
}
