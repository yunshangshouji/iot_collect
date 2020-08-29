package zhuboss.gateway.facade.api.param;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class MeterParam extends BaseParam {
    @NotEmpty
    @ApiModelProperty("关联ID")
    private String refId;

    @NotEmpty
    @ApiModelProperty("关联采集器ID")
    private String collectorRefId;

    @NotEmpty
    @ApiModelProperty(value = "接口类型，字典interface_type:com、tcp、plc",example = "com")
    private String interfaceType;

    @ApiModelProperty(value = "主机",example = "192.168.6.111")
    private String host;

    @ApiModelProperty(value = "端口号",example = "8888")
    private Integer port;

    @ApiModelProperty(value = "串口号",example = "1")
    private Integer comPort;

    @ApiModelProperty(value = "地址(modbus、dlt645)",example = "1")
    private Long addr;

    @NotNull
    @ApiModelProperty("设备型号ID，引用字典")
    private Integer meterTypeId;

    @ApiModelProperty("设备名称")
    private String devName;

    @NotNull
    @ApiModelProperty(value = "启用,1:true 0:false",example = "1")
    private Boolean enabled;

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getCollectorRefId() {
        return collectorRefId;
    }

    public void setCollectorRefId(String collectorRefId) {
        this.collectorRefId = collectorRefId;
    }

    public String getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getComPort() {
        return comPort;
    }

    public void setComPort(Integer comPort) {
        this.comPort = comPort;
    }

    public Long getAddr() {
        return addr;
    }

    public void setAddr(Long addr) {
        this.addr = addr;
    }

    public Integer getMeterTypeId() {
        return meterTypeId;
    }

    public void setMeterTypeId(Integer meterTypeId) {
        this.meterTypeId = meterTypeId;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
