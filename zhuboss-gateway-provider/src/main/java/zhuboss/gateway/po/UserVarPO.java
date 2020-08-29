package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("user_var")
@FieldNameConstants(asEnum = true)
public class UserVarPO extends AbstractPO {
    @PrimaryKey
    private Integer id;
    private Integer appId;
    private String varName;
    private String val;
    private Date createTime;
    private Date modifyTime;
}
