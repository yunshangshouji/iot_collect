package zhuboss.gateway.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.SubQueryColumn;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("meter_kind_read")
@FieldNameConstants(asEnum = true)
public class MeterKindReadPO extends AbstractPO {
    @PrimaryKey
    private Integer id;

    private Integer appId;

    @SubQueryColumn("select kind_code from meter_kind where id = meter_kind_id")
    private String meterKind;

    private Integer meterKindId;

    private String targetCode;

    private String targetName;

    private Integer signalFlag;

    private Integer scale;

    private String unit;

    private Integer persistFlag;

    private Date createTime;

    private Date modifyTime;

    @ApiModelProperty("dlt645-2007/1997数据标识")
    private String dlt645;
}