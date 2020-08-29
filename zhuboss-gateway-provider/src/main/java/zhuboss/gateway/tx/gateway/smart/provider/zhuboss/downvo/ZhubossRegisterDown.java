package zhuboss.gateway.tx.gateway.smart.provider.zhuboss.downvo;

import lombok.Data;

@Data
public class ZhubossRegisterDown extends DownMessage {
    public static final int success = 0;
    public static final int fail = 1;

    private int result;
    private long timestamp;
    private String msg;

    public ZhubossRegisterDown(int result, long timestamp, String msg) {
        this.result = result;
        this.timestamp = timestamp;
        this.msg = msg;
    }
}
