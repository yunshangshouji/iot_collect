package zhuboss.gateway.util.mail;

import java.io.Serializable;

public class SendEmailMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7823440749721742917L;
	
	private String address;
	private String subject;
	private String content;
	

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

}
