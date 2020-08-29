package zhuboss.gateway.facade.api.param;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;

public class CollectorParam extends BaseParam {
    /**
     * 采集器关联ID
     */
    @NotEmpty
    @ApiModelProperty("网关(关联ID)")
    private String refId;

    /**
     * 站点关联ID，空默认根站点
     */
    @ApiModelProperty("站点(关联ID)")
    private String stationRefId;

    @NotEmpty
    @ApiModelProperty("网关号")
    private String devNo;

    @ApiModelProperty("网关名称")
    private String devName;

    @NotEmpty
    @ApiModelProperty(value = "网关类型，字典：collector_type",example = "1")
    private String collectorType;

    @NotEmpty
    @ApiModelProperty("密钥")
    private String secretKey;

    @ApiModelProperty("上报周期，空采用项目默认")
    private Integer reportPeriod;

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getStationRefId() {
        return stationRefId;
    }

    public void setStationRefId(String stationRefId) {
        this.stationRefId = stationRefId;
    }

    public String getDevNo() {
        return devNo;
    }

    public void setDevNo(String devNo) {
        this.devNo = devNo;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getCollectorType() {
        return collectorType;
    }

    public void setCollectorType(String collectorType) {
        this.collectorType = collectorType;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Integer getReportPeriod() {
        return reportPeriod;
    }

    public void setReportPeriod(Integer reportPeriod) {
        this.reportPeriod = reportPeriod;
    }
}
