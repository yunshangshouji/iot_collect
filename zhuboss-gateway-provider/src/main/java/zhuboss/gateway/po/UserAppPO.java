package zhuboss.gateway.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;
import zhuboss.framework.mybatis.mapper.TableAlias;

import java.util.Date;

@Data
@Table("user_app join app on user_app.app_id = app.app_id " +
        "left join user on user_app.user_id = user.id")
@FieldNameConstants(asEnum = true)
@TableAlias("user_app")
public class UserAppPO extends AbstractPO {
    @PrimaryKey
    private Integer id;
    private Integer userId;

    @TableAlias("user")
    private String mail;
    @TableAlias("user")
    private String nickName;

    private Integer appId;
    @TableAlias("app")
    private String appName;
    private Integer ownerFlag;
    private Integer pushFlag;
    private Integer cfgFlag;
    private Date createTime;
    private Date modifyTime;
}
