package zhuboss.gateway.po;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class MeterValues {
    private Integer meterId;
    private String lastValues;

    private JSONObject jsonObject;

    public Object getValue(String targetCode){
        if(jsonObject == null){
            jsonObject = JSON.parseObject(lastValues);
        }
        if(jsonObject == null){
            return null;
        }
        return jsonObject.get(targetCode);
    }
}
