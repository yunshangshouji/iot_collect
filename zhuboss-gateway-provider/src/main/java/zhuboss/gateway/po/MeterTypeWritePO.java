package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.TypeAlias;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.SubQueryColumn;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("meter_type_write")
@FieldNameConstants(asEnum = true)
public class MeterTypeWritePO extends AbstractPO {
    @PrimaryKey
    private Integer id;

    private Integer appId;

    private Integer meterTypeId;

    private Integer meterKindWriteId;

    @SubQueryColumn("select target_code from meter_kind_write where id = meter_kind_write_id")
    private String targetCode;

    @SubQueryColumn("SELECT target_name FROM meter_kind_write where id = meter_kind_write_id")
    private String targetName;

    private Integer cmd;
    private String addr;
    private String writeUnits;
    private String writeByteSize;
    private String dataHex;
    private String remark;
    private Date createTime;
    private Date modifyTime;
}
