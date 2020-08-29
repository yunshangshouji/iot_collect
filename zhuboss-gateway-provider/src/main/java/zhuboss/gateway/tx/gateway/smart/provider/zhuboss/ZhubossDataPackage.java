package zhuboss.gateway.tx.gateway.smart.provider.zhuboss;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.downvo.DownMessage;

import java.nio.charset.Charset;

@Data
public class ZhubossDataPackage {
    private byte type;
    private byte[] data;

    public static final Charset charset = Charset.forName("UTF-8");

    public ZhubossDataPackage() {
    }

    public ZhubossDataPackage(byte type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public ZhubossDataPackage(byte type, String text) {
        this.type = type;
        this.data = text.getBytes(charset);
    }

    public ZhubossDataPackage(byte type, DownMessage downMessage){
        this.type = type;
        this.data = JSON.toJSONString(downMessage).getBytes(charset);
    }

}
