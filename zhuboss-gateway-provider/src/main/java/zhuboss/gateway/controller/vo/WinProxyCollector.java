package zhuboss.gateway.controller.vo;

import lombok.Data;

import java.util.List;

@Data
public class WinProxyCollector {
    private String collectorNo;

    private String collectorName;

    private List<WinProxyDev> devs;
}
