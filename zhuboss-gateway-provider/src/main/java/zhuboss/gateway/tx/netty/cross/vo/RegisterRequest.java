package zhuboss.gateway.tx.netty.cross.vo;

import lombok.Data;

@Data
public class RegisterRequest {
    private String userName;
    private String loginPwd;
    /**
     * PLC device id
     */
    private Integer devId;

    /**
     * 以下为超级用户
     */
    private String gwNo;
    private String addr;
    private Integer port;
}
