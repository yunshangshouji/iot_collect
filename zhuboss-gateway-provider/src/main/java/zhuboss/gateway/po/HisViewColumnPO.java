package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.SubQueryColumn;
import zhuboss.framework.mybatis.mapper.Table;
import zhuboss.framework.mybatis.mapper.TableAlias;

import java.util.Date;

@Data
@Table("his_view_column LEFT JOIN meter_kind_read ON meter_kind_read_id = meter_kind_read.id")
@TableAlias("his_view_column")
@FieldNameConstants(asEnum = true)
public class HisViewColumnPO extends AbstractSortablePO {
    @PrimaryKey
    private Integer id;
    private Integer appId;
    private Integer viewId;

    @TableAlias("meter_kind_read")
    private String targetName;

    private Integer meterKindReadId;
    private Integer seq;
    private Integer width;
    private Date createTime;
    private Date modifyTime;

    @Override
    public Integer get_Id() {
        return this.id;
    }

    @Override
    public Integer get_Seq() {
        return this.seq;
    }

    @Override
    public void set_Seq(Integer seq) {
        this.seq = seq;
    }

    @Override
    public Object get_GroupId() {
        return this.viewId;
    }
}
