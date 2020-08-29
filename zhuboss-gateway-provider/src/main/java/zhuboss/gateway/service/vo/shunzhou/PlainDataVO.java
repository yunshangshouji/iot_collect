package zhuboss.gateway.service.vo.shunzhou;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Map;

@Data
public class PlainDataVO {
    //"03030000000000000004"
    private String id;
    /**
     * 仪表是否在线
     */
    private Boolean ol;

    private Map<String,Object> st;

}
