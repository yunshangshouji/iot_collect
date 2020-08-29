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
@Table("meter_type")
@FieldNameConstants(asEnum = true)
public class MeterTypePO extends AbstractPO {
    @PrimaryKey
    private Integer id;

    private Integer appId;

    private String typeName;

    @SubQueryColumn("select kind_code from meter_kind where id = meter_kind_id")
    private String meterKind;

    private Integer meterKindId;

    private String protocol;

    @ApiModelProperty("波特率")
    private Integer baudRate;

    @ApiModelProperty("校验位")
    private String parity;

    @ApiModelProperty("数据位")
    private Integer byteSize;

    @ApiModelProperty("停止位")
    private Integer stopBits;

    @ApiModelProperty("读取响应时间")
    private Integer readMillSeconds;

    private String remark;

    private Date createTime;

    private Date modifyTime;

    private Byte aliveFlag;

}