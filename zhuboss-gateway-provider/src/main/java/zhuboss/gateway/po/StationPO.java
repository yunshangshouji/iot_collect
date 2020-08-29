package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.NotColumn;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;
import zhuboss.gateway.util.TreeEntity;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Table("station")
@FieldNameConstants(asEnum = true)
public class StationPO extends TreeEntity {
    @PrimaryKey
    private Integer id;
    private Integer appId;
    private String refId;
    private String fullText;
    private BigDecimal lng;
    private BigDecimal lat;
    private Date createTime;
    private Date modifyTime;

    @NotColumn
    private String iconCls;
}
