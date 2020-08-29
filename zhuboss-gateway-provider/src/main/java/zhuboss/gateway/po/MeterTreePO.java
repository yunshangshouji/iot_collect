package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;
import zhuboss.framework.mybatis.mapper.TableAlias;
import zhuboss.gateway.util.TreeEntity;

import java.util.Date;

@Data
@Table("meter_tree LEFT JOIN meter ON meter_tree.`meter_id` = meter.`id` ")
@TableAlias("meter_tree")
@FieldNameConstants(asEnum = true)
public class MeterTreePO extends TreeEntity {
    @PrimaryKey
    private Integer id;
    private Integer appId;

    private Integer stationId;

    private String type;
    private Integer meterId;
    private Integer iconClsId;
    private Integer isSubDiagram;
    private Date createTime;
    private Date modifyTime;

    @TableAlias("meter")
    private String interfaceType;
    @TableAlias("meter")
    private Integer comPort;
    @TableAlias("meter")
    private Long addr;
    @TableAlias("meter")
    private String ip;
    @TableAlias("meter")
    private Integer port;
    @TableAlias("meter")
    private String devName;

    public String getIconCls(){
       if(this.type.equals("DIR")){
           return "icon-zhu_folder";
       }else{
           return "icon-zhu_device";
       }
    }

    //for echarts tree
    public String getName(){
        return this.getText();
    }

}
