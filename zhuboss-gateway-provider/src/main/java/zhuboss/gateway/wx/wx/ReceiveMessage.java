package zhuboss.gateway.wx.wx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 接收来自微信服务器的信息
 * @author Administrator
 *
 */
@XmlRootElement(name="xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReceiveMessage {
	/**
	 * base
	 */
	@XmlElement(name="ToUserName")
	private String toUserName;
	
	@XmlElement(name="FromUserName")
	private String fromUserName;
	
	@XmlElement(name="CreateTime")
	private Long createTime;
	
	@XmlElement(name="MsgType")
	private String msgType;
	
	@XmlElement(name="Event")
	private String event;
	
	@XmlElement(name="EventKey")
	private String eventkey;
	
	@XmlElement(name="MsgId")
	private Long msgId;
	
	/**
	 * text
	 */
	@XmlElement(name="Content")
	private String content;
	
	/**
	 * image
	 */
	@XmlElement(name="PicUrl")
	private String picUrl;
	/**
	 * image,invoice,video
	 */
	@XmlElement(name="mediaId")
	private String MediaId;
	/**
	 * invoice
	 */
	@XmlElement(name="Format")
	private String format;
	/**
	 * video
	 */
	@XmlElement(name="ThumbMediaId")
	private String thumbMediaId;
	
	/**
	 * link
	 */
	@XmlElement(name="Title")
	private String title;
	
	@XmlElement(name="Description")
	private String description;
	
	@XmlElement(name="Url")
	private String url;
	
	/**
	 * location
	 */
	@XmlElement(name="Location_X")
	private Double location_X;
	
	@XmlElement(name="Location_Y")
	private Double location_Y;
	
	@XmlElement(name="Scale")
	private Integer scale;
	
	@XmlElement(name="Label")
	private String label;
	
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
	public Long getMsgId() {
		return msgId;
	}
	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getMediaId() {
		return MediaId;
	}
	public void setMediaId(String mediaId) {
		MediaId = mediaId;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getThumbMediaId() {
		return thumbMediaId;
	}
	public void setThumbMediaId(String thumbMediaId) {
		this.thumbMediaId = thumbMediaId;
	}
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Double getLocation_X() {
		return location_X;
	}
	public void setLocation_X(Double location_X) {
		this.location_X = location_X;
	}
	public Double getLocation_Y() {
		return location_Y;
	}
	public void setLocation_Y(Double location_Y) {
		this.location_Y = location_Y;
	}
	public Integer getScale() {
		return scale;
	}
	public void setScale(Integer scale) {
		this.scale = scale;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getEventkey() {
		return eventkey;
	}
	public void setEventkey(String eventkey) {
		this.eventkey = eventkey;
	}
}
