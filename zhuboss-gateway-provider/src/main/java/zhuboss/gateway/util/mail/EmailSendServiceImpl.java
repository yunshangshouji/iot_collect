package zhuboss.gateway.util.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import zhuboss.framework.exception.BussinessException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;

@Service
@Slf4j
public class EmailSendServiceImpl implements IEmailSendService {
	@Value("${mail.from}")
	String mailFrom;
	@Autowired
    JavaMailSender mailSender;
	
	@Override
	public void sendEmail(SendEmailMessage sendEmailMessage) throws UnsupportedEncodingException, MessagingException {

		// 建立邮件讯息
		MimeMessage mailMessage = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper;
		try {
			messageHelper = new MimeMessageHelper(mailMessage,true,"utf-8");
		} catch (MessagingException e1) {
			throw new RuntimeException(e1);
		}

		// 设定收件人、寄件人、主题与内文
		try {
			String MAIL_SENDER= "内容";
			String sender = MimeUtility.encodeText(MAIL_SENDER);
			messageHelper.setFrom(mailFrom);
			messageHelper.setSubject(sendEmailMessage.getSubject());
			messageHelper.setText(sendEmailMessage.getContent(), true);
			messageHelper.setTo(sendEmailMessage.getAddress());
			mailSender.send(mailMessage);


		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

}
