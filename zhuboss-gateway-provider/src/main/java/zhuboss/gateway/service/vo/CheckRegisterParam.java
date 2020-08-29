package zhuboss.gateway.service.vo;

import lombok.Data;

@Data
public class CheckRegisterParam {
    private String gwid;
    private Long timestamp;
    private Long dev_version;
    private String sign;
}
