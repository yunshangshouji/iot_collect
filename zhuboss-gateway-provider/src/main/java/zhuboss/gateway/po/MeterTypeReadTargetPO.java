package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.*;

import java.util.Date;

@Data
@Table("meter_type_read_target")
@FieldNameConstants(asEnum = true)
public class MeterTypeReadTargetPO extends AbstractPO {
    @PrimaryKey
    private Integer id;

    private Integer appId;

    private Integer meterTypeId;

    private Integer meterKindReadId;

    @SubQueryColumn("SELECT target_code FROM meter_kind_read WHERE id = meter_kind_read_id")
    private String targetCode;

    @SubQueryColumn("SELECT target_name FROM meter_kind_read WHERE id = meter_kind_read_id")
    private String targetName;

    private String valueType;

    private Integer readId;

    private Integer addr;

    private String addrHex;

//    private BigDecimal ratio;

    private String ratiovar;

    private String expression;

    private Date createTime;

    private Date modifyTime;

}