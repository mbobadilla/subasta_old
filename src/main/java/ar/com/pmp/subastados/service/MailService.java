package ar.com.pmp.subastados.service;

import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import ar.com.pmp.subastados.domain.Subscriber;
import ar.com.pmp.subastados.domain.User;
import io.github.jhipster.config.JHipsterProperties;

/**
 * Service for sending emails.
 * <p>
 * We use the @Async annotation to send emails asynchronously.
 */
@Service
public class MailService {

	private final Logger log = LoggerFactory.getLogger(MailService.class);

	private static final String USER = "user";

	private static final String BASE_URL = "baseUrl";

	private final JHipsterProperties jHipsterProperties;

	private final JavaMailSender javaMailSender;

	private final MessageSource messageSource;

	private final SpringTemplateEngine templateEngine;

	@Value("${admin.mail}")
	private String adminMail;

	public MailService(JHipsterProperties jHipsterProperties, JavaMailSender javaMailSender,
			MessageSource messageSource, SpringTemplateEngine templateEngine) {

		this.jHipsterProperties = jHipsterProperties;
		this.javaMailSender = javaMailSender;
		this.messageSource = messageSource;
		this.templateEngine = templateEngine;
	}

	public boolean sendEmailMessageSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart, isHtml, to, subject, content);

		// Prepare message using a Spring helper
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
			message.setTo(to);
			message.setFrom(jHipsterProperties.getMail().getFrom());
			message.setSubject(subject);
			message.setText(content, isHtml);
			javaMailSender.send(mimeMessage);
			log.debug("Sent email to User '{}'", to);
		} catch (Exception e) {
			log.warn("Message could not be sent to user '{}': {}", to, e.getMessage());
			return false;
		}
		return true;
	}
	
	@Async(value = "notificationExecutor")
	public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart, isHtml, to, subject, content);

		// Prepare message using a Spring helper
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
			message.setTo(to);
			message.setFrom(jHipsterProperties.getMail().getFrom());
			message.setSubject(subject);
			message.setText(content, isHtml);
			javaMailSender.send(mimeMessage);
			log.debug("Sent email to User '{}'", to);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.warn("Email could not be sent to user '{}'", to, e);
			} else {
				log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
			}
		}
	}

	@Async(value = "notificationExecutor")
	public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
		Locale locale = Locale.forLanguageTag(user.getLangKey());
		Context context = new Context(locale);
		context.setVariable(USER, user);
		context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
		String content = templateEngine.process(templateName, context);
		String subject = messageSource.getMessage(titleKey, null, locale);
		sendEmail(user.getEmail(), subject, content, false, true);
	}

	@Async(value = "notificationExecutor")
	public void sendActivationEmail(User user) {
		log.debug("Sending validation email to '{}'", user.getEmail());
		sendEmailFromTemplate(user, "activationEmail", "email.activation.title");
	}

	@Async(value = "notificationExecutor")
	public void sendCreationEmail(User user) {
		log.debug("Sending creation email to '{}'", user.getEmail());
		sendEmailFromTemplate(user, "creationEmail", "email.activation.title");
	}

	@Async(value = "notificationExecutor")
	public void sendPasswordResetMail(User user) {
		log.debug("Sending password reset email to '{}'", user.getEmail());
		sendEmailFromTemplate(user, "passwordResetEmail", "email.reset.title");
	}

	@Async(value = "notificationExecutor")
	public void sendSocialRegistrationValidationEmail(User user, String provider) {
		log.debug("Sending social registration validation email to '{}'", user.getEmail());
		Locale locale = Locale.forLanguageTag(user.getLangKey());
		Context context = new Context(locale);
		context.setVariable(USER, user);
		context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
		context.setVariable("provider", StringUtils.capitalize(provider));
		String content = templateEngine.process("socialRegistrationValidationEmail", context);
		String subject = messageSource.getMessage("email.social.registration.title", null, locale);
		sendEmail(user.getEmail(), subject, content, false, true);
	}

	@Async(value = "notificationExecutor")
	public void sendSubscribeEmail(Subscriber subscriber) {
		log.debug("Sending new subscriber welcome email to '{}'", subscriber.getEmail());

		Locale locale = Locale.forLanguageTag("es");
		Context context = new Context(locale);
		context.setVariable("subscriber", subscriber);
		context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
		String content = templateEngine.process("subscribeEmail", context);
		String subject = messageSource.getMessage("email.subscribe.title", null, locale);
		sendEmail(subscriber.getEmail(), subject, content, false, true);
	}

	@Async(value = "notificationExecutor")
	public void sendSubscribeEmailToAdmin(Subscriber subscriber) {
		log.debug("Sending new Subscriber data to '{}'", adminMail);

		Locale locale = Locale.forLanguageTag("es");
		Context context = new Context(locale);
		context.setVariable("subscriber", subscriber);
		context.setVariable("date", ZonedDateTime.ofInstant(subscriber.getCreatedDate(), TimeZone.getDefault().toZoneId()).toString());
		context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
		String content = templateEngine.process("adminSubscribeEmail", context);
		String subject = messageSource.getMessage("email.admin.subscribe.title", null, locale);
		sendEmail(adminMail, subject, content, false, true);
	}

	public void sendMailToAdmin(String subject, String content) {
		log.debug("Sending new mail to Admin ({}): '{}'", adminMail, content);
		sendEmail(adminMail, subject, content, false, true);
	}

	public void sendEmailToUser(User user, String subject, String content) {
		if (user.isEmailValid() && user.isEmailNotifications() && StringUtils.isNotBlank(user.getEmail())) {
			log.debug("Sending notification email to '{}'", user.getEmail());

			Locale locale = Locale.forLanguageTag("es");
			Context context = new Context(locale);
			context.setVariable("user", user);
			context.setVariable("content", content);
			context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
			String mailContent = templateEngine.process("userNotificationEmail", context);
			String mailSubject = messageSource.getMessage("email.user.notification.title", null, locale);
			sendEmail(user.getEmail(), mailSubject, mailContent, false, true);
		}
	}
}
