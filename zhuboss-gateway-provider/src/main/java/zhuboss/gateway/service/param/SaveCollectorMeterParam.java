package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SaveCollectorMeterParam {

    private String refId;

    private Integer comPort;

    /**
     * lora终端地址
     */
    private Integer loraAddr;

    private String host;
    private Integer port;

    /**
     * 设备表号
     */
    private Long addr;

    private String devName;
    @NotNull
    private Integer meterTypeId;

    @NotNull
    private Integer enabled;

}
