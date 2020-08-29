package zhuboss.gateway.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.SubQueryColumn;
import zhuboss.framework.mybatis.mapper.Table;

import java.math.BigDecimal;
import java.util.Date;

@Data
@FieldNameConstants(asEnum = true)
public class BaseOverLimitPO extends LogMeter {
    @PrimaryKey
    private String id;

    @ApiModelProperty("告警设置ID")
    private Long meterAlarmId;

    @ApiModelProperty("参数")
    private String var;
    @ApiModelProperty("参数名称")
    private String varName;
    @ApiModelProperty("起始值")
    private BigDecimal fromValue;
    @ApiModelProperty("结束值")
    private BigDecimal toValue;
    @ApiModelProperty("报警值")
    private BigDecimal readValue;

    @ApiModelProperty("描述")
    private String title;

}
