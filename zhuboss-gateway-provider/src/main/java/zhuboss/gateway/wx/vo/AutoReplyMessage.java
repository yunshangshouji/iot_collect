package zhuboss.gateway.wx.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 向微信服务器发送的信息
 * @author Administrator
 *
 */

@XmlRootElement(name="xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class AutoReplyMessage {
	
	@XmlElement(name="ToUserName")
	private String toUserName;
	
	@XmlElement(name="FromUserName")
	private String fromUserName;
	
	@XmlElement(name="CreateTime")
	private Long createTime;
	
	@XmlElement(name="MsgType")
	private String msgType;
	
	@XmlElement(name="Content")
	private String content;
	
	@XmlElement(name="ArticleCount")
	private Integer articleCount;

	@XmlElement(name="item")
	@XmlElementWrapper(name="Articles")
	private List<Article> articles;
	
  
	public Integer getArticleCount() {
		if(articles!=null) 
			return articles.size();
		return 0;
	}
	public void setArticleCount(Integer articleCount) {
		this.articleCount = articleCount;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}
 
}
