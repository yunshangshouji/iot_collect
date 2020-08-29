package zhuboss.gateway.tx.gateway.smart.provider.zhuboss.downvo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ZhubossRead {
    private int cmd;
    private int startAddr;
    private int len;
    private List<ZhubossProfile> profiles = new ArrayList<>();
}
