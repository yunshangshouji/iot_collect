package zhuboss.gateway.wx.vo;

import com.alibaba.fastjson.annotation.JSONField;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="item")
@XmlAccessorType(XmlAccessType.FIELD)
public class Article {
	
	@XmlElement(name="Title")
	private String title;
	
	@XmlElement(name="Description")
	private String description;
	
	@XmlElement(name="PicUrl")
	@JSONField(name = "picurl")
	private String picUrl;
	
	@XmlElement(name="Url")
	private String url;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
