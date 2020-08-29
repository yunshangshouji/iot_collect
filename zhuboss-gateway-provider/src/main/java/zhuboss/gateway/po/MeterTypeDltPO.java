package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.SubQueryColumn;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("meter_type_dlt")
@FieldNameConstants(asEnum = true)
public class MeterTypeDltPO extends AbstractPO {
    @PrimaryKey
    private Integer id;

    private Integer meterTypeId;

    @SubQueryColumn("SELECT target_code FROM `meter_kind_read` WHERE id = meter_type_dlt.`meter_kind_read_id`")
    private String targetCode;

    @SubQueryColumn("SELECT target_name FROM `meter_kind_read` WHERE id = meter_type_dlt.`meter_kind_read_id`")
    private String targetName;

    @SubQueryColumn("SELECT dlt645 FROM `meter_kind_read` WHERE id = meter_type_dlt.`meter_kind_read_id`")
    private String dlt2007;

    @SubQueryColumn("SELECT dlt645.`item1997` FROM `meter_kind_read`,dlt645 WHERE meter_kind_read.`dlt645`= dlt645.`item2007` AND id = meter_type_dlt.`meter_kind_read_id`")
    private String dlt1997;

    private Integer meterKindReadId;

    private Date createTime;

    private Date modifyTime;
}
