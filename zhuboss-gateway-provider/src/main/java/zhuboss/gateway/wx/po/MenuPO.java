package zhuboss.gateway.wx.po;

import lombok.Data;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.NotColumn;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("wx_menu")
public class MenuPO extends AbstractPO {
	@PrimaryKey
	private Integer id;
	private String name;
	private Integer level;
	private Integer pid;
	@NotColumn
	private String pidName;
	private Integer seq;
	private String type;
	private String url;
	@NotColumn
	private Integer replyId;
	private Date createDate;
	private Date modifyDate;

}
