package zhuboss.gateway.controller.vo;

import lombok.Data;

import java.util.List;

@Data
public class WinProxyLoginResult {
    private Boolean success;
    private String errmsg;
    private List<WinProxyCollector> collectors;

    public WinProxyLoginResult(Boolean success, String errmsg, List<WinProxyCollector> collectors) {
        this.success = success;
        this.errmsg = errmsg;
        this.collectors = collectors;
    }
}
