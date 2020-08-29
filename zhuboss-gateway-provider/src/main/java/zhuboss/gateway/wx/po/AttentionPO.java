package zhuboss.gateway.wx.po;

import lombok.Data;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;


@Data
@Table("wx_attention")
public class AttentionPO extends AbstractPO {
	@PrimaryKey
	private String openid;
	private String nickname;
	private String sex;
	private String city;
	private String province;
	private String country;
	private String headimgurl;

	private Date subscribeTime;

	private Date createTime;
	private Date modifyTime;

}
