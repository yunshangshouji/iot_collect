package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("summary_panel")
@FieldNameConstants(asEnum = true)
public class SummaryPanelPO extends AbstractSortablePO {
    @PrimaryKey
    private Integer id;
    private Integer appId;
    private Integer summaryId;
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
        return this.getSummaryId();
    }
}
