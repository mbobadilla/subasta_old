package ar.com.pmp.subastados.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.pmp.subastados.config.ScheduleFutureTasks;
import ar.com.pmp.subastados.config.ScheduleFutureTasks.LoteState;
import ar.com.pmp.subastados.domain.Event;
import ar.com.pmp.subastados.domain.Lote;
import ar.com.pmp.subastados.events.RefreshEvent;
import ar.com.pmp.subastados.repository.EventRepository;
import ar.com.pmp.subastados.repository.LoteRepository;
import ar.com.pmp.subastados.repository.UserRepository;
import ar.com.pmp.subastados.security.SecurityUtils;
import ar.com.pmp.subastados.service.dto.LoteDTO;
import ar.com.pmp.subastados.web.rest.errors.BadRequestException;
import ar.com.pmp.subastados.web.rest.errors.EntityNotFoundException;

@Service
@Transactional
public class LoteService {

	private final Logger log = LoggerFactory.getLogger(LoteService.class);

	@Autowired
	private LoteRepository loteRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private ScheduleFutureTasks scheduleFutureTasks;
	@Autowired
	private ApplicationEventPublisher publisher;

	@Transactional(readOnly = true)
	public LoteDTO findById(Long id) {
		log.info("LoteService:: getting lote by id [{}]", id);
		Lote lote = loteRepository.findOne(id);
		return new LoteDTO(lote);
	}

	@Transactional
	public void addFollower(Long id) {
		SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(user -> {
			log.info("LoteService:: add follower to lote by id [{}]", id);
			Lote lote = loteRepository.findOne(id);
			lote.getFollowers().add(user);
			loteRepository.save(lote);

			cacheManager.getCache(EventRepository.EVENT_BY_ID_CACHE).evict(lote.getEvent().getId());
		});
	}

	@Transactional
	public void removeFollower(Long id) {
		SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(user -> {
			log.info("LoteService:: remove follower from lote by id [{}]", id);
			Lote lote = loteRepository.findOne(id);
			lote.getFollowers().remove(user);
			loteRepository.save(lote);

			cacheManager.getCache(EventRepository.EVENT_BY_ID_CACHE).evict(lote.getEvent().getId());
		});
	}

	@Transactional
	public void postponeLoteEndDate(Long loteId, Long minutes) {
		if (minutes == null || minutes <= 0) {
			throw new BadRequestException("La cantidad de minutos a incrementar debe ser mayor a 0");
		}

		Lote lote = loteRepository.findOne(loteId);

		if (lote == null) {
			throw new EntityNotFoundException("Lote no existente.");
		}

		Event event = lote.getEvent();
		if (!event.isActive()) {
			throw new BadRequestException("Evento terminado.");
		}

		// No esta finalizado ?
		if (lote.isFinished()) {
			throw new BadRequestException("El lote esta finalizado.");
		}
		
		if(LoteState.WAITING.equals(ScheduleFutureTasks.statusMap.get(loteId))) {
			throw new BadRequestException("El lote esta en espera.");
		}

		doPostpone(lote, event.getId(), minutes);
	}

	private void doPostpone(Lote lote, Long eventId, Long minutes) {
		Instant newEndDate = lote.getEndDate().plus(minutes, ChronoUnit.MINUTES);
		lote.setEndDate(newEndDate);

		cacheManager.getCache(EventRepository.EVENT_BY_ID_CACHE).evict(eventId);
		loteRepository.save(lote);

		// refrescar future task
		scheduleFutureTasks.updateFutureScheduler(lote, true);

		// Refrescar lote en evento
		publisher.publishEvent(new RefreshEvent(lote, null));
	}
	
	public void doPostponeFromNow(Long loteId, Long minutes) {
		Lote lote = loteRepository.findOne(loteId);
		
		Instant newEndDate = Instant.now().plus(minutes, ChronoUnit.MINUTES);
		lote.setEndDate(newEndDate);

		loteRepository.save(lote);
		
		cacheManager.getCache(EventRepository.EVENT_BY_ID_CACHE).evict(lote.getEvent().getId());
		
		// refrescar future task
		scheduleFutureTasks.updateFutureScheduler(lote, false);
		
		// Refrescar lote en evento
		publisher.publishEvent(new RefreshEvent(lote, null));
	}
}
