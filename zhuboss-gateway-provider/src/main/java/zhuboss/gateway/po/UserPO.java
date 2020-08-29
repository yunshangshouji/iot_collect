package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("user")
@FieldNameConstants(asEnum = true)
public class UserPO extends AbstractPO {
    @PrimaryKey
    private Integer id;
    private String mail;
    private String mobile;
    private String loginPwd;
    private String nickName;
    private String openid;
    private String registerIp;
    private String knowChannel;
    private String verifyCode;
    private Integer validFlag;
    private Integer aliveFlag;
    private Date createTime;
    private Date modifyTime;
}
