package zhuboss.gateway.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.*;

import java.util.Date;

@Data
@Table("app LEFT JOIN user_app ON app.`app_id` = user_app.`app_id` AND user_app.`owner_flag` = 1 ")
@TableAlias("app")
@FieldNameConstants(asEnum = true)
public class AppPO extends AbstractPO {
    @PrimaryKey
    private Integer appId;
    private Integer userId;
    private String appKey;
    private String appName;

    @ApiModelProperty("上报周期间隔")
    private Integer cycleSeconds;

    @ApiModelProperty("网关离线未活动时间")
    private Integer gwLostSeconds;

    private Date createTime;
    private Date modifyTime;

    @SubQueryColumn("SELECT GROUP_CONCAT(user.`mail`) FROM user_app,USER  WHERE user.`id` = user_app.`user_id` AND user_app.`cfg_flag`=1 AND  app_id = app.app_id")
    private String cfgUsers;

    @SubQueryColumn("SELECT GROUP_CONCAT(user.`mail`) FROM user_app,USER  WHERE user.`id` = user_app.`user_id` AND user_app.`cfg_flag`=0 AND  app_id = app.app_id")
    private String browserUsers;
}
