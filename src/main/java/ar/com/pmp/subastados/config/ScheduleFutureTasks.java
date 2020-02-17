package ar.com.pmp.subastados.config;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import ar.com.pmp.subastados.domain.Event;
import ar.com.pmp.subastados.domain.EventType;
import ar.com.pmp.subastados.domain.GeneralConfiguration;
import ar.com.pmp.subastados.domain.GeneralConfigurationKey;
import ar.com.pmp.subastados.domain.Lote;
import ar.com.pmp.subastados.events.LastMinutesNotificationEvent;
import ar.com.pmp.subastados.repository.EventRepository;
import ar.com.pmp.subastados.repository.GeneralConfigurationRepository;
import ar.com.pmp.subastados.repository.LoteRepository;
import ar.com.pmp.subastados.service.BidService;
import ar.com.pmp.subastados.service.EventService;
import ar.com.pmp.subastados.service.LoteService;

@Component
public class ScheduleFutureTasks implements ApplicationListener<ApplicationReadyEvent> {

	private final Logger log = LoggerFactory.getLogger(ScheduleFutureTasks.class);

	@Autowired
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;

	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private EventService eventService;
	@Autowired
	private BidService bidService;
	@Autowired
	private LoteService loteService;
	@Autowired
	private LoteRepository loteRepository;

	@Autowired
	private ApplicationEventPublisher publisher;
	@Autowired
	private GeneralConfigurationRepository generalConfigurationRepository;

	private static ScheduledFuture<?> eventNotificationScheduled = null;
	private static Map<Long, ScheduledFuture<?>> loteFinalizeScheduled = Collections.synchronizedMap(Maps.newHashMap());
	public static Map<Long, LoteState> statusMap = Collections.synchronizedMap(Maps.newHashMap());

	public enum LoteState {
		ACTIVE, // Activo, por default
		WAITING, // Esperando para finalizar
		FINISHED // Finalizado
	}

	/**
	 * This event is executed as late as conceivably possible to indicate that
	 * the application is ready to service requests.
	 */
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent applicationReadyEvent) {
		Long lastId = eventRepository.getMaxId();
		if (lastId != null) {
			Event event = eventRepository.findOne(lastId);
			if (event.isActive()) {
				log.info("Programando triggers de finalizacion del evento: [{}]", event);
				scheduleLastMinutesEventNotification(event);
				scheduleFinalizeLotes(event);
			}
		}
	}

	public void scheduleLastMinutesEventNotification(Event event) {
		Instant now = Instant.now();
		Instant endDate = event.getEndDate();
		Instant eventTriggerTime = endDate.minus(event.getNotificateLastMinutes(), ChronoUnit.MINUTES);

		// Schedulo si todavia no paso ese tiempo y esta activo
		if (event.isActive() && now.isBefore(eventTriggerTime)) {

			// cancelo el viejo scheduler
			if (eventNotificationScheduled != null) {
				eventNotificationScheduled.cancel(true);
				eventNotificationScheduled = null;
			}

			final Long eventId = event.getId();

			eventNotificationScheduled = threadPoolTaskScheduler.schedule(() -> {
				log.info("++++ Evento de ultimos minutos lanzado para evento: {}", eventId);
				publisher.publishEvent(new LastMinutesNotificationEvent(eventRepository.findOne(eventId)));
			}, Date.from(eventTriggerTime));

			log.info("-- Programado evento de notificacion de ultimos minutos para {}", eventTriggerTime.atZone(ZoneOffset.systemDefault()));
		}
	}

	public void scheduleFinalizeLotes(Event event) {
		Instant now = Instant.now();
		if (event.isActive()) {

			// Cancelo los viejos schedulers
			loteFinalizeScheduled.forEach((key, value) -> {
				if (value != null) {
					value.cancel(true);
				}
			});
			loteFinalizeScheduled.clear();
			statusMap.clear();

			event.getLotes().forEach(lote -> {
				if (now.isBefore(lote.getEndDate())) {
					ScheduledFuture<?> future = scheduleLote(lote);
					loteFinalizeScheduled.put(lote.getId(), future);
					statusMap.put(lote.getId(), LoteState.ACTIVE);
				} else {
					statusMap.put(lote.getId(), LoteState.FINISHED);
				}
			});
		}
	}

	// Se ejecuta al actualizar la fecha de finalizacion de un lote - 
	// checkActive = false ---> siempre se schedulea
	// checkActive = true ---> se schedulea si el lote esta ACTIVO
	public void updateFutureScheduler(Lote lote, boolean checkActive) {
		if(!checkActive || LoteState.ACTIVE.equals(statusMap.get(lote.getId()))) {
			ScheduledFuture<?> scheduledFuture = loteFinalizeScheduled.get(lote.getId());
			if (scheduledFuture != null && !scheduledFuture.isDone() && !scheduledFuture.isCancelled()) {
				scheduledFuture.cancel(true);
			}
			
			ScheduledFuture<?> scheduleLote = scheduleLote(lote);
			loteFinalizeScheduled.put(lote.getId(), scheduleLote);
		}
	}

	private ScheduledFuture<?> scheduleLote(final Lote lote) {
		final Long loteId = lote.getId();

		ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(() -> {
			finishOrWait(loteId);
		}, Date.from(lote.getEndDate()));

		log.info("-- Programado evento de finalizacion de lote [{}] para {}", loteId, lote.getEndDate().atZone(ZoneOffset.systemDefault()));

		return future;
	}

	private void finishOrWait(final Long loteId) {
		final Lote lote = loteRepository.findOne(loteId); //Obtengo el objeto actualizado de la base
		final Event event = lote.getEvent();
		final Long eventId = event.getId();
		final EventType type = event.getType();

		log.info("++++ Evento de Finzalizacion de lote: {}, evento {}, tipo finalizacion {}, orden {}", loteId, eventId, type, lote.getOrden());

		switch (type) {
		case ESCALONADO:

			boolean canFinish = false;
			List<Lote> lotes = event.getLotes().stream().sorted().collect(Collectors.toList());

			int myIndex = lotes.indexOf(lote);

			if (myIndex == 0) { // Es el primero
				canFinish = true;
			} else {
				int previousIndex = myIndex - 1;
				Lote previousLote = lotes.get(previousIndex);

				LoteState previousState = statusMap.get(previousLote.getId());

				switch (previousState) { // El estado del anterior lote
				case ACTIVE:
					// pasa a WAITING
					statusMap.put(loteId, LoteState.WAITING);
					break;

				case FINISHED:
					canFinish = true;
					break;

				case WAITING:
					// Pasa a WAITING
					statusMap.put(loteId, LoteState.WAITING);
					break;
				}
			}

			if (canFinish) {
				statusMap.put(loteId, LoteState.FINISHED);
				bidService.finishLote(loteId);

				// Proximo lote
				if (myIndex != lotes.size() - 1) { // No es el ultimo lote

					int nextIndex = myIndex + 1;
					Lote nextLote = lotes.get(nextIndex);

					LoteState nextState = statusMap.get(nextLote.getId());
					if (LoteState.WAITING.equals(nextState)) { // Si el proximo lote esta WAITING
						// Se pospone X minutos
						GeneralConfiguration minutesToAddConfig = generalConfigurationRepository.findOne(GeneralConfigurationKey.MINUTES_TO_ADD_ON_FINISH.name());
						Long minutesToAdd = Optional.ofNullable(minutesToAddConfig).map(x -> Long.valueOf(x.getValue())).orElse(5L);

						// Importante para poder posponer ante nuevas ofertas
						statusMap.put(nextLote.getId(), LoteState.ACTIVE);
						
						loteService.doPostponeFromNow(nextLote.getId(), minutesToAdd);
					}
				}
			}

			break;
		case INDEPENDIENTE:

			statusMap.put(loteId, LoteState.FINISHED);
			bidService.finishLote(loteId);

			break;
		}

		// cuando todos terminen hay que marcar como finalizado el evento
		boolean allFinished = statusMap.values().stream().allMatch(state -> LoteState.FINISHED.equals(state));
		if (allFinished) {
			eventService.finishEvent(eventId);
		}

	}

}
