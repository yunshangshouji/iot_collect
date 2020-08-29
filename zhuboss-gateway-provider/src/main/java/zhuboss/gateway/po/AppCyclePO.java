package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.util.StringUtils;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.SubQueryColumn;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("app_cycle")
@FieldNameConstants(asEnum = true)
public class AppCyclePO extends AbstractPO {
    @PrimaryKey
    private Integer id;
    private Integer appId;
    private Integer stationId;

    @SubQueryColumn("SELECT TEXT FROM station WHERE id = app_cycle.`station_id`")
    private String stationName;
    private Integer meterKindId;
    private Integer meterTypeId;
    private Integer cycleSeconds;
    private String remark;
    private Date createTime;
    private Date modifyTime;

    public int getLevel(){
        if(stationId!=null && meterTypeId!=null){
            return 1;
        }
        if(stationId!=null && meterKindId != null){
            return 2;
        }
        if(stationId!=null){
            return 3;
        }
        if(meterTypeId != null){
            return 4;
        }
        if(meterKindId != null){
            return 5;
        }
        return 6;
    }
}
