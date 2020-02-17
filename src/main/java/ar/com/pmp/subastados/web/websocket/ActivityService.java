package ar.com.pmp.subastados.web.websocket;

import static ar.com.pmp.subastados.config.WebsocketConfiguration.IP_ADDRESS;

import java.security.Principal;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import ar.com.pmp.subastados.events.FinishLote;
import ar.com.pmp.subastados.events.RefreshEvent;
import ar.com.pmp.subastados.events.ReloadEvent;
import ar.com.pmp.subastados.web.websocket.dto.ActivityDTO;

@Controller
public class ActivityService implements ApplicationListener<SessionDisconnectEvent> {

	private static final Logger log = LoggerFactory.getLogger(ActivityService.class);

	private final SimpMessageSendingOperations messagingTemplate;

	public ActivityService(SimpMessageSendingOperations messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	// Admin service for user tracking

	@SubscribeMapping("/topic/activity")
	@SendTo("/topic/tracker")
	public ActivityDTO sendActivity(@Payload ActivityDTO activityDTO, StompHeaderAccessor stompHeaderAccessor, Principal principal) {
		activityDTO.setUserLogin(principal.getName());
		activityDTO.setSessionId(stompHeaderAccessor.getSessionId());
		activityDTO.setIpAddress(stompHeaderAccessor.getSessionAttributes().get(IP_ADDRESS).toString());
		activityDTO.setTime(Instant.now());
		log.debug("Sending user tracking data {}", activityDTO);
		return activityDTO;
	}

	@Override
	public void onApplicationEvent(SessionDisconnectEvent event) {
		ActivityDTO activityDTO = new ActivityDTO();
		activityDTO.setSessionId(event.getSessionId());
		activityDTO.setPage("logout");
		messagingTemplate.convertAndSend("/topic/tracker", activityDTO);
	}

	// Bid refresh utility

	@EventListener
	public void sendEventActivity(RefreshEvent refreshEvent) {
		log.debug("RefreshEvent: {}", refreshEvent.getSource());
		messagingTemplate.convertAndSend("/topic/event-tracker", refreshEvent.getSource());
	}
	
	// Reload event utility
	
	@EventListener
	public void sendReloadEventActivity(ReloadEvent reloadEvent) {
		log.debug("ReloadEvent: {}", reloadEvent.getSource());
		messagingTemplate.convertAndSend("/topic/event-reload", reloadEvent.getSource());
	}
	
	// Finish lote utility
	
	@EventListener
	public void sendFinishLoteActivity(FinishLote finishLote) {
		log.debug("FinishLote: {}", finishLote.getSource());
		messagingTemplate.convertAndSend("/topic/finish-lote", finishLote.getSource());
	}
}
