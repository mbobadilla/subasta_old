package ar.com.pmp.subastados.web.rest;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import ar.com.pmp.subastados.security.AuthoritiesConstants;
import ar.com.pmp.subastados.service.EventService;
import ar.com.pmp.subastados.service.dto.EventDTO;
import ar.com.pmp.subastados.service.export.EventExport;
import ar.com.pmp.subastados.web.rest.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class EventController {

	private final Logger log = LoggerFactory.getLogger(EventController.class);

	@Autowired
	private EventService eventService;
	@Autowired
	private EventExport eventExport;

	@GetMapping("/event-management")
	@Timed
	public ResponseEntity<List<EventDTO>> getAllEvents(Pageable pageable) {
		final Page<EventDTO> page = eventService.getAll(pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/event-management");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	@GetMapping("/event-management/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public EventDTO findEventById(@PathVariable("id") Long id) {
		log.debug("Find event by id [{}]", id);
		EventDTO eventDTO = eventService.findById(id);
		log.debug("Returning: ", eventDTO.toString());
		return eventDTO;
	}

	@DeleteMapping("/event-management/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public void deleteEventById(@PathVariable("id") Long id) {
		log.debug("Find event by id [{}]", id);
		eventService.deleteById(id);
	}

	@PostMapping("/event-management")
	@Secured(AuthoritiesConstants.ADMIN)
	@ResponseStatus(code = HttpStatus.OK)
	public EventDTO saveEvent(@RequestBody EventDTO eventDTO) throws Exception {
		log.debug("Create event: name [{}], endDate: [{}]", eventDTO.getName(), eventDTO.getEndDate());
		return eventService.createEvent(eventDTO);
	}

	@PutMapping("/event-management")
	@Secured(AuthoritiesConstants.ADMIN)
	@ResponseStatus(code = HttpStatus.OK)
	public EventDTO updateEvent(@RequestBody EventDTO eventDTO) throws Exception {
		log.debug("Update event: name [{}], endDate: [{}]", eventDTO.getName(), eventDTO.getEndDate());
		return eventService.updateEvent(eventDTO);
	}

	// --------------------------------------------------------

	@GetMapping("/event")
	@ResponseStatus(code = HttpStatus.OK)
	public List<EventDTO> getAll() {
		log.debug("Find all events");
		List<EventDTO> list = eventService.findAll();
		log.debug("Returning: ", list.toString());
		return list;
	}

	@GetMapping("/event/history")
	@ResponseStatus(code = HttpStatus.OK)
	public List<EventDTO> getAllWithoutLote() {
		log.debug("Find all events without Lote");
		List<EventDTO> list = eventService.findAllWithoutLote();
		log.debug("Returning: ", list.toString());
		return list;
	}

	@GetMapping("/event/combo")
	@ResponseStatus(code = HttpStatus.OK)
	public List<EventDTO> getAllToCombo() {
		return eventService.findAllToCombo();
	}

	@GetMapping("/event/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public EventDTO findById(@PathVariable("id") Long id) {
		log.debug("Find event by id [{}]", id);
		EventDTO eventDTO = eventService.findById(id);
		log.debug("Returning: ", eventDTO.toString());
		return eventDTO;
	}

	@GetMapping("/event/last")
	@ResponseStatus(code = HttpStatus.OK)
	public EventDTO findLast() {
		log.debug("Find last event");
		EventDTO eventDTO = eventService.findLast();
		log.debug("Returning: ", eventDTO.toString());
		return eventDTO;
	}

	@GetMapping("/event/last/name")
	@ResponseStatus(code = HttpStatus.OK)
	public String findLastEventName() {
		log.debug("Find last event name");
		EventDTO eventDTO = eventService.findLast();
		log.debug("Returning: ", eventDTO.toString());
		return eventDTO.getName();
	}

	@PostMapping("/event/")
	@ResponseStatus(code = HttpStatus.OK)
	public EventDTO createEvent(@RequestParam("name") String name,
			@RequestParam("initDate") String initDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("notificateLastMinutes") Integer notificateLastMinutes,
			@RequestParam(name = "categoria", defaultValue = "ofertaOnline") String categoria) throws Exception {
		log.debug("Create event: name [{}], endDate: [{}]", name, endDate);

		LocalDateTime localInitDate = DateTimeFormatter.ISO_DATE_TIME.parse(initDate, LocalDateTime::from);
		Instant initInstant = ZonedDateTime.of(localInitDate, ZoneOffset.systemDefault()).toInstant();

		LocalDateTime localEndDate = DateTimeFormatter.ISO_DATE_TIME.parse(endDate, LocalDateTime::from);
		Instant endInstant = ZonedDateTime.of(localEndDate, ZoneOffset.systemDefault()).toInstant();

		return eventService.createEvent(name, initInstant, endInstant, notificateLastMinutes, categoria);
	}

	@RequestMapping(path = "/event/excel/{id}", method = RequestMethod.GET)
	public ResponseEntity<Resource> export(@PathVariable("id") Long id, HttpServletResponse response) {
		try (Workbook wb = eventExport.buildExcelDocument(id)) {
			if (wb != null) {
				String name = "Evento_" + id + ".xls";
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				wb.write(baos);
				
				ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Disposition", "attachment; filename=" + name);
				headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
				headers.add("Pragma", "no-cache");
				headers.add("Expires", "0");

				return ResponseEntity.ok().headers(headers).contentLength(resource.contentLength()).contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(resource);
			}
		} catch (Exception e) {
			log.error("Error al generar el excel de evento id: " + id, e);
		}
		
		return ResponseEntity.badRequest().build();
	}
	
}
