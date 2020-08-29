package zhuboss.gateway.service.param;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class PersistOption implements Serializable {
    private Integer persistInterval;
    private String persistUnit;
    private Integer persistDays;
    private List<String> cols = new ArrayList<>();
    private List<String> targetCodes = new ArrayList<>();

    public int getPersistSeconds(){
        return persistUnit.equals("s")? persistInterval : (persistUnit.equals("m")?persistInterval*60 : persistInterval*60*60);

    }

}
