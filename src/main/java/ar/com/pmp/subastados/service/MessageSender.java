package ar.com.pmp.subastados.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import ar.com.pmp.subastados.domain.ActivityEvent;
import ar.com.pmp.subastados.domain.ActivityEventType;
import ar.com.pmp.subastados.domain.Message;
import ar.com.pmp.subastados.domain.MessageState;
import ar.com.pmp.subastados.domain.MessageType;
import ar.com.pmp.subastados.domain.MessageUser;
import ar.com.pmp.subastados.domain.MessageUserState;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.events.UserActivityEvent;

@Component
public class MessageSender {

	private final Logger log = LoggerFactory.getLogger(MessageSender.class);

	@Autowired
	private ThreadPoolTaskScheduler messageSenderTaskScheduler;

	private static ScheduledFuture<?> senderScheduled = null;

	private static List<MessageTask> runList = Collections.synchronizedList(new ArrayList<MessageTask>());

	private static final String NAME_PLACE_HOLDER = "##name##";
	private static final String LAST_NAME_PLACE_HOLDER = "##lastName##";
	private static final String LOGIN_PLACE_HOLDER = "##login##";

	@Autowired
	private MessageService messageService;
	@Autowired
	private ApplicationEventPublisher publisher;
	@Autowired
	private MailService mailService;
	@Autowired
	private WhatsappService whatsappService;

	public void sendActivityEvent(Message message, MessageUser destinatary) {
		ActivityEvent activityEvent = ActivityEventType.MESSAGE.createActivityEvent(destinatary.getUser().getLogin(), message.getId().toString(), message);
		publisher.publishEvent(new UserActivityEvent(activityEvent));
	}

	@EventListener
	public void onApplicationEvent(ApplicationReadyEvent event) {
		// Al iniciar la app, deberia retomarse cualquier envio inconcluso
		log.info("Enviar mensajes pendientes");

		List<Message> list = messageService.findByState(MessageState.SENDING);

		list.stream().forEach(message -> scheduleSend(message));
	}

	@Async
	public void scheduleSend(Message message) {
		// Filtro los mensajes a enviar
		List<MessageUser> toSend = message.getDestinataries().stream().filter(mu -> MessageUserState.WAITING.equals(mu.getState())).collect(Collectors.toList());

		// Creo sublistas de 5 elementos
		List<List<MessageUser>> splitted = Lists.partition(toSend, 5);

		// Creo tasks por cada sublista
		splitted.forEach(list -> {
			addRunnable(new MessageTask(message, list));
		});

		// Si ya existe un scheduler los tasks se van a ejecutar en algun momento, si no existe creo uno nuevo
		if (senderScheduled == null) {
			TimerTask repeatedTask = new TimerTask() {
				public void run() {
					if (!runList.isEmpty()) {
						runList.remove(0).run();
					} else {
						senderScheduled.cancel(false);
						senderScheduled = null;
					}
				}
			};
			senderScheduled = messageSenderTaskScheduler.scheduleAtFixedRate(repeatedTask, 5000L);
		}
	}

	private void addRunnable(MessageTask r) {
		runList.add(r);
	}

	protected class MessageTask implements Runnable {
		private Message message;
		private List<MessageUser> destinataries;

		MessageTask(Message message, List<MessageUser> destinataries) {
			this.message = message;
			this.destinataries = destinataries;
		}

		public void run() {
			MessageType type = message.getType();
			destinataries.forEach(destinatary -> {
				switch (type) {
				case MAIL:
					mailSender(message, destinatary);
					break;
				case PHONE:
					whatsappSender(message, destinatary);
					break;
				}
			});
		}
	}

	private String getBody(Message message, MessageUser destinatary) {
		User user = destinatary.getUser();
		return message.getBody().replaceAll(NAME_PLACE_HOLDER, user.getFirstName()).replaceAll(LAST_NAME_PLACE_HOLDER, user.getLastName()).replaceAll(LOGIN_PLACE_HOLDER, user.getLogin());
	}

	// ************************** WHATSSAPP

	public void whatsappSender(Message message, MessageUser destinatary) {
		// XXX Se comento a pedido de Esteban, siempre se envia mensajes, aunque el tel no este validado
		// if (destinatary.getUser().isPhoneValid() && destinatary.getUser().isPhoneNotifications()) {
		destinatary.incrementAttempts();
		if (sendWhatsapp(message, destinatary)) {
			destinatary.setState(MessageUserState.SENDED);
			sendActivityEvent(message, destinatary);
		} else {
			destinatary.setState(MessageUserState.SENDING_ERROR);
		}
		// } else {
		// destinatary.setState(MessageUserState.CONTACT_ERROR);
		// }

		messageService.updateMessageUser(destinatary);
	}

	private boolean sendWhatsapp(Message message, MessageUser destinatary) {
		return whatsappService.sendWhatsappMessage(destinatary.getUser().getCellPhone(), getBody(message, destinatary));
	}

	// ************************** Mail

	public void mailSender(Message message, MessageUser destinatary) {
		// XXX Se comento a pedido de Esteban, siempre se envia mensajes, aunque el mail no este validado
		// if (destinatary.getUser().isEmailValid() && destinatary.getUser().isEmailNotifications()) {
		destinatary.incrementAttempts();
		if (sendMail(message, destinatary)) {
			destinatary.setState(MessageUserState.SENDED);
			sendActivityEvent(message, destinatary);
		} else {
			destinatary.setState(MessageUserState.SENDING_ERROR);
		}
		// } else {
		// destinatary.setState(MessageUserState.CONTACT_ERROR);
		// }

		messageService.updateMessageUser(destinatary);
	}

	private boolean sendMail(Message message, MessageUser destinatary) {
		String content = getBody(message, destinatary);
		return mailService.sendEmailMessageSync(destinatary.getUser().getEmail(), message.getSubject(), content, false, message.isHtml());
	}

}
