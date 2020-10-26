package mas.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


public class MailService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
	private String user;
	private String pass;	
	private Session session;

	@SuppressWarnings("unused")
	private MailService() {}

	public MailService(String user,String pass) {
		this.user = user;
		this.pass = pass;

		Properties props = System.getProperties();	
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		
		session = Session.getInstance(props,
				new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass);
			}
		});
	}
	
	public void sendMessageWithAttachmentTo(List<String> recepients, String filePath) {
		final String subject = "Automated tests result";
		
		
		MimeMessage message = new MimeMessage(session);	
		
		try {	

			message.setFrom(new InternetAddress(user));
			message.setSubject(subject);		

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("Automated tests result in attachment");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			messageBodyPart = new MimeBodyPart();			
			DataSource source = new FileDataSource(filePath);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName("report.pdf");
			messageBodyPart.setHeader("Content-Type", "application/pdf");
			
			multipart.addBodyPart(messageBodyPart);

			message.setContent(multipart);

			List<Address> formatted = new ArrayList<>();

			for(String recepient : recepients) {
				formatted.add(new InternetAddress(recepient));
			}

			Transport.send(message, formatted.toArray(new Address[formatted.size()]));

		} catch (AddressException e) {
			LOGGER.error("Can't send message. Reason: " + e.getMessage());
		} catch (MessagingException e) {
			LOGGER.error("Can't send message. Reason: " + e.getMessage());
		}
	}

	public void sendMessageWithLinkTo(List<String> recepients, String link) {
		final String subject = "Automated tests result";		
		
		MimeMessage message = new MimeMessage(session);	
		
		try {	

			message.setFrom(new InternetAddress(user));
			message.setSubject(subject);		

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("Automated tests result: \n " + link);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			message.setContent(multipart);

			List<Address> formatted = new ArrayList<>();

			for(String recepient : recepients) {
				formatted.add(new InternetAddress(recepient));
			}

			Transport.send(message, formatted.toArray(new Address[formatted.size()]));

		} catch (AddressException e) {
			LOGGER.error("Can't send message. Reason: " + e.getMessage());
		} catch (MessagingException e) {
			LOGGER.error("Can't send message. Reason: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		MailService mailer = new MailService("i.bai@test.pro", "test");

		List<String> recepients = Arrays.asList(new String[]{"ivan.bai.sergeevich@gmail.com"});

		mailer.sendMessageWithLinkTo(recepients, "www.redtube.com");
	}
}
