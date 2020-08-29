package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.SubQueryColumn;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("meter_kind_write")
@FieldNameConstants(asEnum = true)
public class MeterKindWritePO extends AbstractPO {
    @PrimaryKey
    private Integer id;

    private Integer appId;

    @SubQueryColumn("select kind_code from meter_kind where id = meter_kind_id")
    private String meterKind;

    private Integer meterKindId;

    private String targetCode;

    private String targetName;

    private Date createTime;
    private Date modifyTime;
}
