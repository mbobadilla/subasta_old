package ar.com.pmp.subastados.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.pmp.subastados.domain.ActivityEvent;
import ar.com.pmp.subastados.domain.ActivityEventType;
import ar.com.pmp.subastados.domain.Event;
import ar.com.pmp.subastados.domain.EventParticipant;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.events.UserActivityEvent;
import ar.com.pmp.subastados.repository.EventParticipantRepository;
import ar.com.pmp.subastados.repository.EventRepository;
import ar.com.pmp.subastados.repository.UserRepository;
import ar.com.pmp.subastados.web.rest.errors.BadRequestException;
import ar.com.pmp.subastados.web.rest.errors.EntityNotFoundException;

@Service
@Transactional
public class EventParticipantService {

	private final Logger log = LoggerFactory.getLogger(EventParticipantService.class);

	@Autowired
	private EventParticipantRepository eventParticipantRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private ApplicationEventPublisher publisher;

	private void participate(String login, Long eventId) {
		log.info("Solicitud de nuevo participante: {}, evento {}", login, eventId);

		Optional<User> userOptional = userRepository.findOneByLogin(login);
		User user = userOptional.orElseThrow(() -> new EntityNotFoundException("Usuario invalido"));
		Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Evento invalido"));

		if (user.isParticipant(eventId)) { // Ya es participante, retorna OK
			return;
		}

		if (!event.isActive()) {
			throw new BadRequestException("Evento terminado.");
		}

		eventParticipantRepository.save(new EventParticipant(user, eventId));

		// Creo evento de actividad de usuario
		ActivityEvent activityEvent = ActivityEventType.EVENT.createActivityEvent(user.getLogin(), null, event);
		publisher.publishEvent(new UserActivityEvent(activityEvent));

		cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
		cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());

		log.info("Nuevo participante generado: {}, evento {}", login, eventId);
	}

	public void participateToLastEvent(String login) {
		Long lastId = eventRepository.getMaxId();
		if (lastId == null) {
			throw new EntityNotFoundException("No hay eventos");
		}
		participate(login, lastId);
	}

}
