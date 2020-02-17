package ar.com.pmp.subastados.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.pmp.subastados.domain.ActivityEvent;
import ar.com.pmp.subastados.domain.ActivityEventType;
import ar.com.pmp.subastados.domain.Lote;
import ar.com.pmp.subastados.events.UserActivityEvent;
import ar.com.pmp.subastados.repository.ActivityEventRepository;
import ar.com.pmp.subastados.repository.LoteRepository;
import ar.com.pmp.subastados.service.dto.ActivityEventDTO;

/**
 * Service class for managing Messages.
 */
@Service
@Transactional
public class ActivityEventService {

	private final Logger log = LoggerFactory.getLogger(ActivityEventService.class);

	@Autowired
	private ActivityEventRepository activityEventRepository;
	@Autowired
	private LoteRepository loteRepository;

	@Async
	@EventListener
	public void createActivityEvent(UserActivityEvent event) {
		log.info("Nuevo evento de actividad de usuario: {}", event.getSource());
		activityEventRepository.save((ActivityEvent) event.getSource());
	}

	@Transactional(readOnly = true)
	public Page<ActivityEventDTO> findByLoginAndType(String login, ActivityEventType[] types, Pageable pageable) {
		return activityEventRepository.findByLoginAndTypeInOrderByDateDesc(login, types, pageable).map(ActivityEventDTO::new);
	}

	public void createProductView(String login, String extra, Long loteId) {
		Lote lote = loteRepository.findOne(loteId);

		ActivityEvent activityEvent = ActivityEventType.PRODUCT_VIEW.createActivityEvent(login, extra, lote);
		activityEventRepository.save(activityEvent);
	}

}
