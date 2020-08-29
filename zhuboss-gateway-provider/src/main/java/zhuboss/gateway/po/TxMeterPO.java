package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Table("tx_meter")
@FieldNameConstants(asEnum = true)
@Data
public class TxMeterPO extends AbstractPO {
    @PrimaryKey
    private Integer meterId;

    private Integer onlineFlag;

    private Date offlineTime;

    private String lastMsgId;
    private Date lastReportTime;
    private Date lastReadTime;
    private String lastValues;
    private String lastErrorMsg;
}
