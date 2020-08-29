package zhuboss.gateway.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.*;

import java.util.Date;

@Table("collector left join tx_collector on id = tx_collector.collector_id")
@FieldNameConstants(asEnum = true)
@Data
@TableAlias("collector")
public class CollectorPO extends AbstractPO {
    @PrimaryKey
    private Integer id;

    private String devNo;

    private String devName;

    private String src;

    @SubQueryColumn("select app_name as appName from app where app.app_id = collector.app_id")
    private String appName;

    private Integer appId;

    private String refId;

    private Integer stationId;

    @SubQueryColumn("SELECT IF(full_text IS NULL  OR full_text ='',TEXT,full_text) FROM station WHERE id = collector.station_id")
    private String stationName;

    /**
     * 采集器类型1宏电无线,2有线,3顺舟
     */
    private String collectorType;

    private String secretKey;

    private Integer reportPeriod;

    /**
     * 信道
     */
//    private Integer loraChan;

    /**
     * 速率
     */
//    private Integer loraSped;

    /**
     * 传输模式
     */
    private Integer loraTransMode;

    @TableAlias("tx_collector")
    private Integer onlineFlag;
    @TableAlias("tx_collector")
    private Date offlineTime;

    private Date createTime;
    private Date modifyTime;

//    private Integer offlineFlag;
//
//    private Date offlineTime;

    @TableAlias("tx_collector")
    private Date lastActiveTime;

    @TableAlias("tx_collector")
    private String devErrorMsg;

    @TableAlias("tx_collector")
    private Date devErrorTime;

    @TableAlias("tx_collector")
    private Date lastOnlineTime;

    @TableAlias("tx_collector")
    private Date appStartTime;

    @TableAlias("tx_collector")
    private String devVersion;

    @TableAlias("tx_collector")
    private String appVersion;

    @TableAlias("tx_collector")
    private String ifName;

    @ApiModelProperty("最后上报时间")
    @TableAlias("tx_collector")
    private Date lastReportTime;

    @ApiModelProperty("最后上报仪表数")
    @TableAlias("tx_collector")
    private Integer lastReportCount;

    @ApiModelProperty("轮询用时")
    @TableAlias("tx_collector")
    private Integer lastLoopSeconds;

    @SubQueryColumn("SELECT COUNT(1) FROM meter WHERE meter.`collector_id` = collector.id")
    private Integer meterCount;

    @SubQueryColumn("SELECT COUNT(1) FROM meter WHERE meter.`collector_id` = collector.id AND meter.`enabled` = 0 ")
    private Integer meterDisableCount;

    @SubQueryColumn("SELECT COUNT(1) FROM meter,tx_meter WHERE meter.`id` = tx_meter.`meter_id` AND meter.`collector_id` = collector.id AND meter.`enabled` = 1 AND tx_meter.`online_flag` = 1")
    private Integer meterOnlineCount;

    public Integer getOfflineCount(){
        return this.meterCount - meterDisableCount - meterOnlineCount;
    }

    //
//    @NotColumn
//    private List<Meter> meterList;

}