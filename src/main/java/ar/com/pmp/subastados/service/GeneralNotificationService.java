package ar.com.pmp.subastados.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import ar.com.pmp.subastados.domain.Authority;
import ar.com.pmp.subastados.domain.Bid;
import ar.com.pmp.subastados.domain.Event;
import ar.com.pmp.subastados.domain.Lote;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.events.BidNotificationEvent;
import ar.com.pmp.subastados.events.EventFinishedNotificationEvent;
import ar.com.pmp.subastados.events.LastMinutesNotificationEvent;
import ar.com.pmp.subastados.events.WinnerNotificationEvent;
import ar.com.pmp.subastados.events.dto.BidNotificationDTO;
import ar.com.pmp.subastados.repository.AuthorityRepository;
import ar.com.pmp.subastados.repository.LoteRepository;
import ar.com.pmp.subastados.repository.UserRepository;
import ar.com.pmp.subastados.security.AuthoritiesConstants;

@Service
public class GeneralNotificationService {

	private final Logger log = LoggerFactory.getLogger(GeneralNotificationService.class);

	private static final String NEW_BID_TEXT = "Hay una nueva oferta por '%s', de U$S %d";
	private static final String WINNER_TEXT = "Tu oferta por '%s' ha ganado, con U$S %d";
	private static final String FINISHED_EVENT_TEXT = "El evento '%s' (con id %d) ha terminado.";

	@Autowired
	private LoteRepository loteRepository;

	@Autowired
	private WhatsappService whatsappService;
	@Autowired
	private PushNotificationService pushNotificationService;
	@Autowired
	private MailService mailService;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthorityRepository authorityRepository;

	@EventListener
	@Async(value = "notificationExecutor")
	public void sendBidNotification(BidNotificationEvent notification) {
		log.info("Nueva notificacion de oferta en lote: {}", notification);
		BidNotificationDTO dto = (BidNotificationDTO) notification.getSource();

		Bid bid = dto.getBid();
		String text = String.format(NEW_BID_TEXT, bid.getLote().getProduct().getNombre(), bid.getPrice().longValue());

		dto.getFollowers().forEach(follower -> {
			// Enviar notificacion por Whatsapp
			if (follower.getActivated() && follower.isPhoneNotifications() && follower.isPhoneValid() && StringUtils.isNotEmpty(follower.getCellPhone()))
				whatsappService.sendMessage(follower.getCellPhone(), text);

			// enviar mail
			if (follower.getActivated() && follower.isEmailNotifications() && follower.isEmailValid())
				mailService.sendEmailToUser(follower, "Solaguayre Venta Online (Nueva oferta)", text);
		});

		// Enviar notificacion push
		pushNotificationService.notificateMessageToUsers(dto.getFollowers(), text);
	}

	@EventListener
	@Async(value = "notificationExecutor")
	public void sendLastMinutesNotification(LastMinutesNotificationEvent notification) {
		Event event = (Event) notification.getSource();
		log.info("Nueva notificacion ultimos momentos del evento: [{}]", event.getName());

		String text = String.format("El evento %s esta por finalizar", event.getName());
		Set<User> allFollowers = event.getLotes().stream().map(Lote::getFollowers).flatMap(Set::stream).collect(Collectors.toSet());

		allFollowers.forEach(follower -> {
			if (follower.getActivated() && follower.isPhoneNotifications() && follower.isPhoneValid() && StringUtils.isNotEmpty(follower.getCellPhone()))
				whatsappService.sendMessage(follower.getCellPhone(), text);

			if (follower.getActivated() && follower.isEmailNotifications() && follower.isEmailValid())
				mailService.sendEmailToUser(follower, "Solaguayre Venta Online (Evento Finalizando)", text);
		});

		// Enviar notificacion push
		pushNotificationService.notificateMessageToUsers(new ArrayList<User>(allFollowers), text);
	}

	@EventListener
	@Async(value = "notificationExecutor")
	public void sendWinnerNotification(WinnerNotificationEvent notification) {
		Long loteId = (Long) notification.getSource();
		log.info("Nueva notificacion ganador de lote: [{}]", loteId);

		Lote lote = loteRepository.findOne(loteId);
		Bid lastBid = lote.getLastBid();

		if (lastBid == null) {
			log.info("El lote [{}] no ha recibido ofertas.", loteId);
			return;
		}

		User user = lastBid.getUser();
		String text = String.format(WINNER_TEXT, lastBid.getLote().getProduct().getNombre(), lastBid.getPrice().longValue());

		log.info("Enviando la siguiente notificacion al usuario {}: {}", user.getLogin(), text);

		// Enviar notificacion por Whatsapp
		if (user.getActivated() && user.isPhoneNotifications() && user.isPhoneValid() && StringUtils.isNotEmpty(user.getCellPhone()))
			whatsappService.sendMessage(user.getCellPhone(), text);

		if (user.getActivated() && user.isEmailNotifications() && user.isEmailValid())
			mailService.sendEmailToUser(user, "Solaguayre Venta Online (Ganador)", text);

		// Enviar notificacion push
		pushNotificationService.notificateMessageToUsers(Lists.newArrayList(user), text);
	}

	@EventListener
	@Async(value = "notificationExecutor")
	public void sendEventFinishedNotification(EventFinishedNotificationEvent notification) {
		Event event = (Event) notification.getSource();
		log.info("ADMIN::: Nueva notificacion evento terminado: [{}]", event);

		String text = String.format(FINISHED_EVENT_TEXT, event.getName(), event.getId());

		// enviar mail a admin
		mailService.sendMailToAdmin("Solaguayre Venta Online (Evento terminado)", text);
	}

	@Async(value = "notificationExecutor")
	public void sendNewUserNotification(User user) {
		log.info("Nuevo usuario creado: {}", user.getLogin());

		Authority adminAuth = authorityRepository.findOne(AuthoritiesConstants.ADMIN);
		List<User> list = userRepository.findAllByActivatedIsTrueAndAuthoritiesIn(Lists.newArrayList(adminAuth));

		String text = String.format("Nuevo usuario registrado [%s], pendiente de validacion", user.getLogin());

		list.stream().forEach(adminUser -> {
			// Enviar notificacion por Whatsapp
			if (adminUser.isPhoneNotifications() && adminUser.isPhoneValid() && StringUtils.isNotEmpty(adminUser.getCellPhone()))
				whatsappService.sendMessage(adminUser.getCellPhone(), text);

			// enviar mail
			if (adminUser.isEmailNotifications() && adminUser.isEmailValid())
				mailService.sendEmailToUser(adminUser, "Solaguayre Venta Online (Nuevo usuario registrado)", text);
		});

	}
}
