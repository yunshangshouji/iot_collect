package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.util.StringUtils;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;
import zhuboss.framework.mybatis.mapper.TableAlias;

import java.util.Date;

@Data
@Table("summary_panel_item \n" +
        "LEFT JOIN meter ON meter_id = meter.`id` \n" +
        "LEFT JOIN collector ON meter.`collector_id` = collector.`id`\n" +
        "LEFT JOIN meter_kind_read ON meter_kind_read_id = meter_kind_read.`id`")
@TableAlias("summary_panel_item")
@FieldNameConstants(asEnum = true)
public class SummaryPanelItemPO extends AbstractSortablePO {
    @PrimaryKey
    private Integer id;
    private Integer appId;
    private Integer summaryId;
    private Integer summaryPanelId;
    private Integer meterId;
    private Integer meterKindReadId;
    private Integer seq;
    private Date createTime;
    private Date modifyTime;

    @TableAlias("meter")
    private String devName;

    @TableAlias("meter")
    private Long addr;

    @TableAlias("collector")
    private String devNo;

    @TableAlias("meter_kind_read")
    private String targetCode;

    @TableAlias("meter_kind_read")
    private String targetName;

    @TableAlias("meter_kind_read")
    private String unit;

    public String getDevNullName(){
        return StringUtils.hasText(this.devName)?devName:("NULL("+ this.devNo +"-"+ this.addr +")");
    }

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
        return this.getSummaryPanelId();
    }
}
