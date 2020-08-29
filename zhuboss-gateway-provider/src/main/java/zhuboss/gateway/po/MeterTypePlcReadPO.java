package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.SubQueryColumn;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("meter_type_plc_read")
@FieldNameConstants(asEnum = true)
public class MeterTypePlcReadPO extends AbstractPO {
    @PrimaryKey
    private Integer id;

    private Integer meterTypeId;

    @SubQueryColumn("SELECT target_code FROM `meter_kind_read` WHERE id = meter_type_plc_read.`meter_kind_read_id`")
    private String targetCode;

    @SubQueryColumn("SELECT target_name FROM `meter_kind_read` WHERE id = meter_type_plc_read.`meter_kind_read_id`")
    private String targetName;

    private Integer meterKindReadId;

    private String addr;

    private String valueType;

    private String ratiovar;

    private Date createTime;

    private Date modifyTime;
}
