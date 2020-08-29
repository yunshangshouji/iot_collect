package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractBizPO;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Table("chart")
@Data
@FieldNameConstants(asEnum = true)
public class ChartPO extends AbstractPO {
    @PrimaryKey
    private Integer id;
    private Integer appId;
    private String chartName;
    private String svg;
    private Date createTime;
    private Date modifyTime;
}
