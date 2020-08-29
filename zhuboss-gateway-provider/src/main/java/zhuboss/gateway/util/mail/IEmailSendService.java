package zhuboss.gateway.util.mail;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface IEmailSendService {
	
	void sendEmail(SendEmailMessage sendEmailMessage) throws UnsupportedEncodingException, MessagingException;
	
}
