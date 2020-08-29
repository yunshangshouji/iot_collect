package zhuboss.gateway.facade.api.param;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class WriteParam extends BaseParam {
    @ApiModelProperty("请求UUID，用于后续追踪")
    @NotEmpty
    private String taskUUID;

    @ApiModelProperty("设备关联ID")
    @NotEmpty
    private String meterRefId;

    @ApiModelProperty("写入编号")
    @NotEmpty
    private String targetCode;

    @ApiModelProperty("写入16进制数据，空使用已默认定义数据")
    private String writeDataHex;

    @ApiModelProperty("是否等待响应结果，同步/异步请求")
    @NotNull
    private Boolean waitResp;

    public String getTaskUUID() {
        return taskUUID;
    }

    public void setTaskUUID(String taskUUID) {
        this.taskUUID = taskUUID;
    }

    public String getMeterRefId() {
        return meterRefId;
    }

    public void setMeterRefId(String meterRefId) {
        this.meterRefId = meterRefId;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    public String getWriteDataHex() {
        return writeDataHex;
    }

    public void setWriteDataHex(String writeDataHex) {
        this.writeDataHex = writeDataHex;
    }

    public boolean isWaitResp() {
        return waitResp;
    }

    public void setWaitResp(boolean waitResp) {
        this.waitResp = waitResp;
    }
}
