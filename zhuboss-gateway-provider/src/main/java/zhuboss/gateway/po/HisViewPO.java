package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.SubQueryColumn;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("his_view")
@FieldNameConstants(asEnum = true)
public class HisViewPO extends AbstractSortablePO {
    @PrimaryKey
    private Integer id;
    private Integer appId;
    private Integer meterKindId;
    @SubQueryColumn("SELECT kind_name FROM meter_kind WHERE id = his_view.meter_kind_id")
    private String meterKindName;

    @SubQueryColumn("SELECT GROUP_CONCAT(target_name) FROM his_view_column ,meter_kind_read WHERE his_view_column.`meter_kind_read_id` = meter_kind_read.id AND his_view_column.`view_id` = his_view.id ORDER BY his_view_column.seq" )
    private String columnNames;

    private String title;
    private Integer seq;
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
        return this.appId;
    }
}
