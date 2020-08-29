package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.NotColumn;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Table("meter_kind")
@FieldNameConstants(asEnum = true)
public class MeterKindPO extends AbstractPO {
    @PrimaryKey
    private Integer id;

    private Integer appId;

    private String kindCode;

    private String kindName;
    private Integer plcFlag;
    private Integer persistFlag;
    private Integer persistInterval;
    private String persistUnit;
    private Integer persistDays;

    private Date createTime;

    private Date modifyTime;

    @NotColumn
    private BigDecimal tableBytes;
    @NotColumn
    private BigDecimal yesterdayTableBytes;
}
