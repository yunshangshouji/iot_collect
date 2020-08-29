package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.util.StringUtils;
import zhuboss.framework.mybatis.mapper.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@FieldNameConstants(asEnum = true)
@Table("meter left join tx_meter on id = tx_meter.meter_id LEFT JOIN collector ON meter.`collector_id` = collector.id left join tx_collector on meter.collector_id = tx_collector.collector_id left join meter_type on meter_type.id = meter.meter_type_id left join station on collector.station_id = station.id")
@TableAlias("meter")
public class MeterPO extends AbstractPO {
    @PrimaryKey
    private Integer id;

    private Integer appId;

    private String refId;

    @TableAlias("collector")
    private Integer stationId;

    @SubQueryColumn("station.full_text")
    private String stationName;

    @TableAlias("collector")
    private String devNo;

    private Integer collectorId;

    private String interfaceType;

    private Integer loraAddr;

    private String host;
    private Integer port;

    private Integer comPort;

    private Long addr;

    @TableAlias("meter_type")
    private Integer meterKindId;
    @SubQueryColumn("SELECT kind_name FROM meter_kind WHERE id = meter_type.`meter_kind_id`")
    private String meterKindName;

    @SubQueryColumn("CONCAT(baud_rate,' ',byte_size,' ',parity,' ',stop_bits)")
    private String serialConfig;

    private Integer meterTypeId;
    @SubQueryColumn("SELECT type_name FROM meter_type WHERE id = meter.meter_type_id")
    private String meterTypeName;

    private String devName;

    private Integer enabled;

    private Date createTime;

    private Date modifyTime;

    //实际轮询间隔
    @NotColumn
    private Integer cycleSeconds;

    @TableAlias("tx_meter")
    private Integer onlineFlag;
    @TableAlias("tx_meter")
    private Date offlineTime;
    @TableAlias("tx_meter")
    private String lastMsgId;
    @TableAlias("tx_meter")
    private Date lastReportTime;
    @TableAlias("tx_meter")
    private Date lastReadTime;
    @TableAlias("tx_meter")
    private String lastValues;
    @TableAlias("tx_meter")
    private String lastErrorMsg;

    /**
     * 名称为空时，返回网关串口
     * @return
     */
    public String getDevNullName(){
        return StringUtils.hasText(this.devName)?devName:(this.devNo +":"+this.comPort+":"+ this.addr );
    }

}
