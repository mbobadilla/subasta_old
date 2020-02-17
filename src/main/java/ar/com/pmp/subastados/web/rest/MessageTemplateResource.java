package ar.com.pmp.subastados.web.rest;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import ar.com.pmp.subastados.domain.MessageTemplate;
import ar.com.pmp.subastados.domain.MessageType;
import ar.com.pmp.subastados.security.AuthoritiesConstants;
import ar.com.pmp.subastados.service.MessageTemplateService;
import ar.com.pmp.subastados.service.dto.MessageTemplateDTO;
import ar.com.pmp.subastados.web.rest.util.HeaderUtil;

@RestController
@RequestMapping("/api")
public class MessageTemplateResource {

	private final Logger log = LoggerFactory.getLogger(MessageTemplateResource.class);

	@Autowired
	private MessageTemplateService messageTemplateService;

	@PostMapping("/messageTemplate")
	@Timed
	public ResponseEntity<Void> createTemplate(@Valid @RequestBody MessageTemplateDTO messageTemplateDTO) {
		log.debug("REST request to save a MessageTemplate : {}", messageTemplateDTO);
		MessageTemplate messageTemplate = messageTemplateService.createTemplate(messageTemplateDTO);
		log.debug("MessageTemplate saved: {}", messageTemplate);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PutMapping("/messageTemplate")
	@Timed
	public ResponseEntity<Void> updateTemplate(@Valid @RequestBody MessageTemplateDTO messageTemplateDTO) {
		log.debug("REST request to update a MessageTemplate : {}", messageTemplateDTO);
		messageTemplateService.updateTemplate(messageTemplateDTO);
		log.debug("MessageTemplate updated");
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("/messageTemplate")
	@Timed
	public ResponseEntity<List<MessageTemplateDTO>> findAllByType(@RequestParam("type") MessageType type,
			@RequestParam("top10") Boolean top10) {
		
		List<MessageTemplateDTO> list;
		if (BooleanUtils.toBoolean(top10)) {
			list = messageTemplateService.findTop10ByType(type);
		} else {
			list = messageTemplateService.findAllByType(type);
		}

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@DeleteMapping("/messageTemplate/{id}")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
		log.debug("REST request to delete a MessageTemplate: {}", id);
		messageTemplateService.deleteTemplate(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("messageTemplate", id.toString()))
				.build();
	}
}
