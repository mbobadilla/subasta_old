package ar.com.pmp.subastados.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.pmp.subastados.config.ScheduleFutureTasks;
import ar.com.pmp.subastados.config.ScheduleFutureTasks.LoteState;
import ar.com.pmp.subastados.domain.ActivityEvent;
import ar.com.pmp.subastados.domain.ActivityEventType;
import ar.com.pmp.subastados.domain.Bid;
import ar.com.pmp.subastados.domain.Event;
import ar.com.pmp.subastados.domain.GeneralConfiguration;
import ar.com.pmp.subastados.domain.GeneralConfigurationKey;
import ar.com.pmp.subastados.domain.Lote;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.events.BidNotificationEvent;
import ar.com.pmp.subastados.events.FinishLote;
import ar.com.pmp.subastados.events.RefreshEvent;
import ar.com.pmp.subastados.events.ReloadEvent;
import ar.com.pmp.subastados.events.UserActivityEvent;
import ar.com.pmp.subastados.events.WinnerNotificationEvent;
import ar.com.pmp.subastados.repository.BidRepository;
import ar.com.pmp.subastados.repository.GeneralConfigurationRepository;
import ar.com.pmp.subastados.repository.LoteRepository;
import ar.com.pmp.subastados.repository.UserRepository;
import ar.com.pmp.subastados.security.SecurityUtils;
import ar.com.pmp.subastados.service.dto.BidDTO;
import ar.com.pmp.subastados.web.rest.errors.BadBidException;
import ar.com.pmp.subastados.web.rest.errors.BadRequestException;
import ar.com.pmp.subastados.web.rest.errors.EntityNotFoundException;
import ar.com.pmp.subastados.web.rest.errors.InternalServerErrorException;

/**
 * Service class for managing Bids.
 */
@Service
@Transactional
public class BidService {

	private final Logger log = LoggerFactory.getLogger(BidService.class);

	@Autowired
	private BidRepository bidRepository;
	@Autowired
	private LoteRepository loteRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private GeneralConfigurationRepository generalConfigurationRepository;

	@Autowired
	private ApplicationEventPublisher publisher;
	@Autowired
	private ScheduleFutureTasks scheduleFutureTasks;

	@Autowired
	private TaskExecutor taskExecutor;
	
	@Autowired
	private MessageSource messageSource;

	@Transactional(readOnly = true)
	public Bid findById(Long id) {
		return bidRepository.findOne(id);
	}

	@Transactional
	public synchronized BidDTO createBid(BidDTO dto) {
		Lote lote = loteRepository.findOne(dto.getLoteId());

		Optional<User> optional = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("Current user login not found")));
		User user = optional.orElseThrow(() -> new EntityNotFoundException("User not found"));

		validateBidCreation(dto, lote, user);

		Bid lastBid = new Bid();
		lastBid.setLote(lote);
		lastBid.setUser(user);
		lastBid.setPrice(dto.getPrice());
		lastBid.setDate(Instant.now());

		lastBid = bidRepository.save(lastBid);

		log.info("Una nueva oferta fue guardada: [{}]", lastBid);

		updateLote(lote, lastBid);

		log.info("Lote actualizado");

		addFollower(lote, user);

		log.info("Agregado como follower");

		BidDTO lastBidDTO = new BidDTO(lastBid);

		sendBidEvents(lote, user, lastBid, lastBidDTO);

		log.info("Enviando notificaciones...");

		// Creo evento de actividad de usuario
		ActivityEvent activityEvent = ActivityEventType.BID.createActivityEvent(user.getLogin(), null, lastBid);
		publisher.publishEvent(new UserActivityEvent(activityEvent));

		return lastBidDTO;
	}

	// Lo hago en este servicio para mantener la finalizacion de los lotes sincronizado con las ofertas
	@Transactional
	public synchronized void finishLote(Long loteId) {
		Lote lote = loteRepository.findOne(loteId);
		lote.setFinished(true);
		loteRepository.save(lote);

		// Evento finish lote a frontend
		publisher.publishEvent(new FinishLote(loteId));

		// Al terminar lote enviar al ganador
		publisher.publishEvent(new WinnerNotificationEvent(loteId));

		// Creo evento de actividad de usuario
		if (lote.getLastBid() != null) {
			ActivityEvent activityEvent = ActivityEventType.PURCHASE.createActivityEvent(lote.getLastBid().getUser().getLogin(), null, lote.getLastBid());
			publisher.publishEvent(new UserActivityEvent(activityEvent));
		}
	}

	// actualiza el lastBid del Lote
	private void updateLote(Lote lote, Bid lastBid) {
		lote.setLastBid(lastBid);

		// Si el lote no esta EN ESPERA
		if (LoteState.ACTIVE.equals(ScheduleFutureTasks.statusMap.get(lote.getId()))) {
			Instant now = Instant.now();
			Instant endDate = lote.getEndDate();
			Instant loteTriggerTime = endDate.minus(lote.getIncrementPeriod(), ChronoUnit.MINUTES);
			if (now.isAfter(loteTriggerTime)) {

				Instant newEndDate = now.plus(lote.getMinutesToIncrement(), ChronoUnit.MINUTES);
				lote.setEndDate(newEndDate);

				// refrescar future task
				scheduleFutureTasks.updateFutureScheduler(lote, true);
			}
		}

		loteRepository.save(lote);
	}

	// marca usuario como follower
	private void addFollower(Lote lote, User user) {
		if (!user.getFollowLotes().contains(lote)) {
			user.getFollowLotes().add(lote);
			userRepository.save(user);
		}
	}

	// Todo se ejecuta en nuevos hilos
	private void sendBidEvents(Lote lote, User user, Bid lastBid, BidDTO lastBidDTO) {
		taskExecutor.execute(() -> {
			// Refrescar lote en evento
			publisher.publishEvent(new RefreshEvent(lote, lastBidDTO));
		});

		// Envio notificaciones a followers menos a este usuario
		List<User> followers = lote.getFollowers().stream().filter(follower -> !user.getId().equals(follower.getId())).collect(Collectors.toList());
		publisher.publishEvent(new BidNotificationEvent(lastBid, followers));

	}

	private void validateBidCreation(BidDTO dto, Lote lote, User user) {
		// Existe el lote ?
		if (lote == null) {
			throw new EntityNotFoundException("No se encuentra el lote seleccionado.");
		}

		Event event = lote.getEvent();
		if (!event.isActive()) { // Se marca inactivo cuando todos los lotes se finalizan
			throw new EntityNotFoundException("Evento terminado.");
		}

		if (Instant.now().isBefore(event.getInitDate())) {
			throw new EntityNotFoundException("Evento no iniciado.");
		}

		// No esta finalizado ?
		if (lote.isFinished()) {
			throw new BadBidException("El lote esta finalizado.");
		}

		// El usuario se encuentra bloqueado
		if (user.isBloqueado()) {
			Locale locale = StringUtils.isNotBlank(user.getLangKey()) ? new Locale(user.getLangKey()) : new Locale("es");
			String message = messageSource.getMessage("bid.user.blocked", null, locale);
			throw new BadRequestException(message);
		}

		// El usuario no es participante
		if (!user.isParticipant(lote.getEvent().getId())) {
			throw new BadRequestException("Usted no es un participante del evento. Haga click en participar para poder ofertar!");
		}

		// Es mayor a la inicial
		if (lote.getLastBid() == null) {
			validateMinimumValue(dto, lote.getInitialPrice());
		} else {
			// Es mayor a la anterior ?
			if (dto.getPrice() <= lote.getLastBid().getPrice()) {
				throw new BadBidException("Hay una oferta existente mejor a la realizada.");
			}

			validateMinimumValue(dto, lote.getLastBid().getPrice());
		}
	}

	// this.oferta >= last.oferta|initialBid * minimo_porcentaje
	private void validateMinimumValue(BidDTO dto, Double currentPrice) {
		try {
			Double minNextBid = getMinNextBid(currentPrice);

			// Es mayor o igual a la oferta minima ?
			if (dto.getPrice() < minNextBid) {
				throw new BadBidException("La oferta debe ser mayor al minimo actual: " + minNextBid.intValue());
			}
		} catch (BadBidException bbe) {
			throw bbe;
		} catch (Exception e) {
			String randomString = UUID.randomUUID().toString();
			log.error("Error en oferta: " + randomString, e);
			throw new BadBidException("Ha ocurrido un error en su oferta, comuniquese con el administrador con este codigo " + randomString);
		}
	}

	private Double getMinNextBid(Double actualPrice) {
		GeneralConfiguration porcentajeAumentoMinimoConfig = generalConfigurationRepository.findOne(GeneralConfigurationKey.PORCENTAJE_MINIMO_MENOR_10000.name());
		GeneralConfiguration aumentoMinimo1000020000Config = generalConfigurationRepository.findOne(GeneralConfigurationKey.MONTO_MINIMO_10000_20000.name());
		GeneralConfiguration aumentoMinimoMayor20000Config = generalConfigurationRepository.findOne(GeneralConfigurationKey.MONTO_MINIMO_MAYOR_20000.name());
		Double minNextBid = null;
		if (actualPrice < 10000D) {
			int procenteajeAumentoMinimo = 5;
			if (porcentajeAumentoMinimoConfig != null && StringUtils.isNumeric(porcentajeAumentoMinimoConfig.getValue()))
				procenteajeAumentoMinimo = Integer.valueOf(porcentajeAumentoMinimoConfig.getValue());
			log.info("Aumento minimo del lote: {}", procenteajeAumentoMinimo);
			Double porcentajeMin = 1D + (procenteajeAumentoMinimo / 100D);
			log.info("Porcentaje minimo de aumento del lote: {}", porcentajeMin);
			minNextBid = actualPrice * porcentajeMin;
		} else if (actualPrice >= 10000D && actualPrice < 20000) {
			int montoMinimo = 500;
			if (aumentoMinimo1000020000Config != null && StringUtils.isNumeric(aumentoMinimo1000020000Config.getValue()))
				montoMinimo = Integer.valueOf(aumentoMinimo1000020000Config.getValue());
			minNextBid = actualPrice + montoMinimo;
		} else {
			int montoMinimo = 1000;
			if (aumentoMinimoMayor20000Config != null && StringUtils.isNumeric(aumentoMinimoMayor20000Config.getValue()))
				montoMinimo = Integer.valueOf(aumentoMinimoMayor20000Config.getValue());
			minNextBid = actualPrice + montoMinimo;
		}

		Double valueDiv100 = minNextBid / 100;
		Integer integerPart = valueDiv100.intValue();
		Double centesimaParte = integerPart * 100D;
		if (centesimaParte.doubleValue() != minNextBid.doubleValue()) {
			minNextBid = centesimaParte + 100;
		}
		log.info("Oferta minima del lote: {}", minNextBid);
		return minNextBid;
	}

	public Page<BidDTO> findByLoteId(Long loteId, Pageable pageable) {
		return bidRepository.findByLoteIdOrderByDateDesc(loteId, pageable).map(BidDTO::new);
	}

	@Transactional
	public synchronized void delete(Lote lote) {
		Bid lastBid = lote.getLastBid();

		// Tempóralmente para poder borrar la oferta
		lote.setLastBid(null);
		loteRepository.save(lote);

		// Borro la ultima oferta
		bidRepository.delete(lastBid);

		// Busco la nueva ultima oferta y actualizo el lote
		Optional<Bid> optional = bidRepository.findTop1ByLoteIdOrderByDateDesc(lote.getId());
		if (optional.isPresent()) {
			Bid newLastBid = optional.get();
			lote.setLastBid(newLastBid);
		} else {
			lote.setLastBid(null);
		}
		loteRepository.save(lote);

		taskExecutor.execute(() -> {
			// Evento reload a clientes
			publisher.publishEvent(new ReloadEvent());

			// Creo evento de actividad de usuario
			ActivityEvent activityEvent = ActivityEventType.DELETE_BID.createActivityEvent(lastBid.getUser().getLogin(), null, lastBid);
			publisher.publishEvent(new UserActivityEvent(activityEvent));
		});
	}

	// public static void main(String[] args) {
	// BidService bidService = new BidService();
	// System.out.println(bidService.getMinNextBid(5000D, 5));
	// System.out.println(bidService.getMinNextBid(5500D, 5));
	// System.out.println(bidService.getMinNextBid(5550D, 5));
	// System.out.println(bidService.getMinNextBid(5600D, 5));
	// System.out.println(bidService.getMinNextBid(10000D, 5));
	// System.out.println(bidService.getMinNextBid(20000D, 5));
	// }
}
