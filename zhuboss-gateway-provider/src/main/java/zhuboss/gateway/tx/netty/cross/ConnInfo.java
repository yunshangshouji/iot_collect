package zhuboss.gateway.tx.netty.cross;

import lombok.Data;

@Data
public class ConnInfo {
    private String gwNo;
    private String addr;
    private Integer port;
}
