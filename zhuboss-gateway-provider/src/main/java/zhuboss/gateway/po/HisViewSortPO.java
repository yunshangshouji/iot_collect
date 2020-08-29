package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.SubQueryColumn;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("his_view_sort")
@FieldNameConstants(asEnum = true)
public class HisViewSortPO extends AbstractSortablePO {
    @PrimaryKey
    private Integer id;
    private Integer appId;
    private Integer viewId;
    @SubQueryColumn("select target_name from meter_kind_read where id = his_view_sort.meter_kind_read_id")
    private String title;
    private Integer meterKindReadId;
    private Integer seq;
    private String sort;
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
