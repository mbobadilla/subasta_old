package ar.com.pmp.subastados.web.rest;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import ar.com.pmp.subastados.domain.Message;
import ar.com.pmp.subastados.domain.MessageState;
import ar.com.pmp.subastados.security.AuthoritiesConstants;
import ar.com.pmp.subastados.service.MessageSender;
import ar.com.pmp.subastados.service.MessageService;
import ar.com.pmp.subastados.service.dto.MessageDTO;
import ar.com.pmp.subastados.web.rest.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class MessageResource {

	private final Logger log = LoggerFactory.getLogger(MessageResource.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	private MessageSender messageSender;

	@PostMapping("/messages")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<Void> createMessage(@Valid @RequestBody MessageDTO messageDTO) {
		log.debug("REST request to save a Message : {}", messageDTO);
		Message message = messageService.createMessage(messageDTO);
		log.debug("Message saved: {}", message);

		messageSender.scheduleSend(message);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/messages")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<List<MessageDTO>> getAllMessages(Pageable pageable) {
		final Page<MessageDTO> page = messageService.getAllMessages(pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/messages");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	@GetMapping("/messages/{id}")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<MessageDTO> findById(@PathVariable Long id) {
		final MessageDTO dto = messageService.findById(id);
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@GetMapping("/messages/{id}/retry")
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<MessageDTO> retry(@PathVariable Long id) {
		Message message = messageService.findToRetry(id);

		if (message.getState().equals(MessageState.SENDING))
			messageSender.scheduleSend(message);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
