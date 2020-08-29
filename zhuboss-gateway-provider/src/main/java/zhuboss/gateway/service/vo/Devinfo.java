package zhuboss.gateway.service.vo;

import lombok.Data;

@Data
public class Devinfo {
    private String id; //"03030000000000000013"

    /**
     * 设备端口，本项目恒定为1
     */
    private final int ep = 1;

    private final DevInfoControl control = new DevInfoControl();
}
