package ar.com.pmp.subastados.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.pmp.subastados.config.ScheduleFutureTasks;
import ar.com.pmp.subastados.domain.Event;
import ar.com.pmp.subastados.domain.GeneralConfiguration;
import ar.com.pmp.subastados.domain.GeneralConfigurationKey;
import ar.com.pmp.subastados.domain.Lote;
import ar.com.pmp.subastados.domain.Product;
import ar.com.pmp.subastados.events.EventFinishedNotificationEvent;
import ar.com.pmp.subastados.repository.EventRepository;
import ar.com.pmp.subastados.repository.GeneralConfigurationRepository;
import ar.com.pmp.subastados.service.dto.EventDTO;
import ar.com.pmp.subastados.service.dto.LoteDTO;
import ar.com.pmp.subastados.service.dto.ProductDTO;
import ar.com.pmp.subastados.service.mapper.EventMapper;
import ar.com.pmp.subastados.service.mapper.ProductMapper;
import ar.com.pmp.subastados.web.rest.errors.EntityNotFoundException;

/**
 * Service class for managing events.
 */
@Service
@Transactional
public class EventService {


	private final Logger log = LoggerFactory.getLogger(EventService.class);

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private EventMapper eventMapper;
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private ScheduleFutureTasks scheduleFutureTasks;
	@Autowired
	private GeneralConfigurationRepository generalConfigurationRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;

	@Value("${global.pdf-event-folder}")
	private String pdfEventFolder;

	@Transactional(readOnly = true)
	public List<EventDTO> findAll() {
		log.info("EventService:: getting all events");
		return eventMapper.persistencesToDTOs(eventRepository.findAll(), true);
	}

	@Transactional(readOnly = true)
	public Page<EventDTO> getAll(Pageable pageable) {
		return eventRepository.findAllByOrderByIdDesc(pageable).map(EventDTO::new);
	}

	@Transactional(readOnly = true)
	public List<EventDTO> findAllWithoutLote() {
		log.info("EventService:: getting all events without lote");
		return eventMapper.persistencesToDTOs(eventRepository.findAllWithoutLotes(), false);
	}

	@Transactional(readOnly = true)
	public List<EventDTO> findAllToCombo() {
		return eventMapper.persistencesToComboDTOs(eventRepository.findAll());
	}

	@Transactional(readOnly = true)
	public EventDTO findById(Long id) throws EntityNotFoundException {
		log.info("EventService:: getting event by id [{}]", id);
		Optional<Event> findById = synchronizedFindById(id);
		findById.orElseThrow(() -> new EntityNotFoundException());
		return eventMapper.persistenceToDTO(findById.get(), true);
	}

	@Transactional
	private synchronized Optional<Event> synchronizedFindById(Long id) {
		return eventRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public EventDTO findLast() {
		log.info("EventService:: getting last event");
		Long lastId = eventRepository.getMaxId();
		if (lastId == null) {
			throw new EntityNotFoundException("No hay eventos");
		}
		return eventMapper.persistenceToDTO(synchronizedFindById(lastId).get(), true);
	}

	@Transactional
	public void finishEvent(Long eventId) {
		Event event = eventRepository.findOne(eventId);
		event.setActive(false);
		eventRepository.save(event);

		// Enviar notificacion de evento finalizado al administrador
		publisher.publishEvent(new EventFinishedNotificationEvent(event));
	}

	@Transactional
	private Event persistEvent(Event event) {
		return eventRepository.save(event);
	}

	@Deprecated
	@Transactional
	public synchronized EventDTO createEvent(String name, Instant initDate, Instant endDate, Integer notificateLastMinutes, String categoria) throws Exception {
		try {

			ObjectMapper mapper = new ObjectMapper();
			URL url = new URL(String.format("", categoria));

			log.info("consumiendo servicio: ", url.toString());

			String content = IOUtils.toString(url, Charset.forName("iso-8859-1")).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "");

			JSONObject destacados = new JSONObject(content);
			JSONArray array = destacados.getJSONArray("$caballos");

			Event event = new Event();
			event.setActive(true);
			event.setInitDate(initDate);
			event.setEndDate(endDate);
			event.setName(name);
			event.setNotificateLastMinutes(notificateLastMinutes);

			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);

				ProductDTO productDTO = mapper.readValue(object.toString(), ProductDTO.class);
				Product product = productMapper.DTOtoPersistence(productDTO);

				Lote lote = new Lote();
				lote.setProduct(product);
				lote.setEvent(event);
				lote.setEndDate(endDate);
				lote.setOrden(product.getOrden());
				lote.setIdcaballo(product.getIdcaballo());

				// default values
				lote.setIncrementPeriod(5);
				lote.setMinutesToIncrement(5);
				lote.setInitialPrice(5000D);

				event.getLotes().add(lote);
			}

			// Antes de guardar el nuevo evento marco los viejos como inactivos
			eventRepository.setInactiveAll();

			Event persistEvent = persistEvent(event);

			log.info("New event: [{}]", persistEvent.toString());

			// Programar eventos futuros
			scheduleFutureTasks.scheduleLastMinutesEventNotification(persistEvent);
			scheduleFutureTasks.scheduleFinalizeLotes(persistEvent);

			cacheManager.getCache(EventRepository.EVENT_BY_ID_CACHE).evict(persistEvent.getId());

			return eventMapper.persistenceToDTO(persistEvent, true);

		} catch (IOException | JSONException e) {
			e.printStackTrace();
			throw e;
		}
	}

	// ------------------------------------------------------------------------------------------------------------

	@Transactional
	public synchronized EventDTO createEvent(EventDTO eventDTO) throws Exception {
		try {

			Event event = new Event();
			event.setActive(true);
			event.setName(eventDTO.getName());
			event.setInitDate(eventDTO.getInitDate());
			event.setEndDate(eventDTO.getEndDate());
			event.setNotificateLastMinutes(eventDTO.getNotificateLastMinutes());
			event.setType(eventDTO.getType());

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			for (LoteDTO loteDto : eventDTO.getLotes()) {
				Lote lote = createLote(event, mapper, loteDto);

				event.getLotes().add(lote);
			}

			// Antes de guardar el nuevo evento marco los viejos como inactivos
			// solo uno activo a la vez, el ultimo creado
			eventRepository.setInactiveAll();

			Event persistEvent = persistEvent(event);

			log.info("New event: [{}]", persistEvent.toString());

			// Borra todos los followers, al crear un nuevo evento, no tiene sentido tener los viejos, un follower se utiliza solo para
			// enviar notificaciones
			eventRepository.deleteAllFollowers();

			// Programar eventos futuros
			scheduleFutureTasks.scheduleLastMinutesEventNotification(persistEvent);
			scheduleFutureTasks.scheduleFinalizeLotes(persistEvent);

			cacheManager.getCache(EventRepository.EVENT_BY_ID_CACHE).evict(persistEvent.getId());

			createFolder(event.getName());

			return eventMapper.persistenceToDTO(persistEvent, true);

		} catch (IOException /* | JSONException */ e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void createFolder(String eventName) {
		try {
			FileUtils.forceMkdir(new File(pdfEventFolder + eventName));
		} catch (IOException e) {
			log.error("Error al crear la carpeta del evento " + eventName, e);
		}
	}

	private void deleteAndCreateFolder(String oldName, String newName) {
		try {
			FileUtils.deleteDirectory(new File(pdfEventFolder + oldName));
			createFolder(newName);
		} catch (IOException e) {
			log.error("Error al borrar la carpeta del evento " + oldName, e);
		}
	}

	private Lote createLote(Event event, ObjectMapper mapper, LoteDTO loteDto) throws Exception {
		GeneralConfiguration urlLoteConfig = generalConfigurationRepository.findOne(GeneralConfigurationKey.URL_LOTE.name());
		String urlLote = Optional.ofNullable(urlLoteConfig).map(GeneralConfiguration::getValue).orElseThrow(() -> new Exception("GeneralConfigurationKey.URL_LOTE is missing on database"));

		URL url = new URL(StringUtils.replace(urlLote, "##ID_CABALLO##", loteDto.getProduct().getIdcaballo()));
		log.info("consumiendo servicio: {}", url.toString());

		String content = IOUtils.toString(url, StandardCharsets.ISO_8859_1).replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "");
		content = StringUtils.stripAccents(content);

		ProductDTO productDTO = mapper.readValue(content, ProductDTO.class);
		Product product = productMapper.DTOtoPersistence(productDTO);

		if (product.getOrden() == null) // en el nuevo servicio no viene el orden, y es requerido
			product.setOrden(loteDto.getOrden());

		// en el nuevo servicio no viene el destacado
		product.setDestacado(loteDto.getProduct().getDestacado());

		Lote lote = new Lote();
		lote.setEvent(event);
		lote.setProduct(product);
		lote.setIdcaballo(product.getIdcaballo());
		lote.setEndDate(event.getEndDate());
		lote.setOrden(loteDto.getOrden());

		lote.setIncrementPeriod(loteDto.getIncrementPeriod());
		lote.setMinutesToIncrement(loteDto.getMinutesToIncrement());
		lote.setInitialPrice(loteDto.getInitialPrice());
		return lote;
	}

	@Transactional
	public synchronized EventDTO updateEvent(EventDTO eventDTO) throws Exception {
		try {

			Long lastId = eventRepository.getMaxId();
			Event event = eventRepository.findById(eventDTO.getId()).orElseThrow(() -> new EntityNotFoundException());

			boolean isLast = lastId == event.getId();

			// Borra el folder con el nombre anterior y crea el nuevo
			if (!StringUtils.equals(event.getName(), eventDTO.getName())) {
				deleteAndCreateFolder(event.getName(), eventDTO.getName());

				event.setName(eventDTO.getName());
			}

			// Solo si es el ultimo evento, y no inició
			if (isLast && event.isActive() && Instant.now().isBefore(event.getInitDate())) {
				event.setInitDate(eventDTO.getInitDate());
				event.setEndDate(eventDTO.getEndDate());
				event.setNotificateLastMinutes(eventDTO.getNotificateLastMinutes());

				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				// Elimino todos los anteriores
				event.getLotes().clear();

				for (LoteDTO loteDto : eventDTO.getLotes()) {
					Lote lote = createLote(event, mapper, loteDto);

					event.getLotes().add(lote);
				}
			}
			Event persistEvent = persistEvent(event);

			log.info("Updated event: [{}]", persistEvent.toString());

			// Schedulo si todavia no paso ese tiempo y esta activo y es el ultimo
			if (isLast && event.isActive() && Instant.now().isBefore(event.getEndDate().minus(event.getNotificateLastMinutes(), ChronoUnit.MINUTES))) {
				scheduleFutureTasks.scheduleLastMinutesEventNotification(persistEvent);
				scheduleFutureTasks.scheduleFinalizeLotes(persistEvent);
			}

			cacheManager.getCache(EventRepository.EVENT_BY_ID_CACHE).evict(persistEvent.getId());

			return eventMapper.persistenceToDTO(persistEvent, true);

		} catch (IOException /* | JSONException */ e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Transactional
	public void deleteById(Long id) {
		Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());

		event.setDeleted(true);
		event.setActive(false);

		cacheManager.getCache(EventRepository.EVENT_BY_ID_CACHE).evict(id);
	}
}
