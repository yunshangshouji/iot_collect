package zhuboss.gateway.wx.wx;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="weixin",ignoreUnknownFields = true)
public class WxConfig {
    String appId;
    String secret;
    String domain;
    String token;
}
