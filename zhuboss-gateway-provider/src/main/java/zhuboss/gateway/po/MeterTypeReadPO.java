package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;
import zhuboss.gateway.util.JavaUtil;

import java.util.Date;

@Data
@Table("meter_type_read")
@FieldNameConstants(asEnum = true)
public class MeterTypeReadPO extends AbstractPO {
    @PrimaryKey
    private Integer id;
    private Integer appId;
    private Integer meterTypeId;
    private Integer cmd;
    private Integer seq;
    private Integer startAddr;
    private Integer len;
    private Integer endAddr;
    private Date createTime;
    private Date modifyTime;



    public String getStartAddrHex(){
        if(startAddr == null) return null;
        return JavaUtil.int2hexString(startAddr,4);
    }

    public String getEndAddrHex(){
        if(endAddr == null) return null;
        return JavaUtil.int2hexString(endAddr,4);
    }

    public String getLenHex(){
        if(len == null) return null;
        return JavaUtil.int2hexString(len,4);
    }
}
