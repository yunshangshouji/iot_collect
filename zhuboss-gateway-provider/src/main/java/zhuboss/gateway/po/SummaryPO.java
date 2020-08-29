package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("summary")
@FieldNameConstants(asEnum = true)
public class SummaryPO extends AbstractSortablePO {
    @PrimaryKey
    private Integer id;
    private Integer appId;
    private String title;
    private Integer seq;
    private Integer refreshInterval;
    private Integer showHeader;
    private Integer showDevName;
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
        return appId;
    }
}
