package ar.com.pmp.subastados.web.rest;

import java.net.URISyntaxException;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import ar.com.pmp.subastados.domain.Subscriber;
import ar.com.pmp.subastados.security.AuthoritiesConstants;
import ar.com.pmp.subastados.service.MailService;
import ar.com.pmp.subastados.service.SubscriberService;
import ar.com.pmp.subastados.service.dto.SubscriberDTO;
import ar.com.pmp.subastados.web.rest.errors.BadRequestAlertException;
import ar.com.pmp.subastados.web.rest.util.HeaderUtil;
import ar.com.pmp.subastados.web.rest.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class SubscriberResource {

	private final Logger log = LoggerFactory.getLogger(SubscriberResource.class);

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private MailService mailService;

	@PostMapping("/subscribers")
	@Timed
	public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody SubscriberDTO subscriberDTO) throws URISyntaxException {
		log.debug("REST request to save Subscriber : {}", subscriberDTO);

		if (subscriberDTO.getId() != null) {
			throw new BadRequestAlertException("A new Subscriber cannot already have an ID", "subscriberManagement", "idexists");
			// } else if (subscriberRepository.findOneByEmailIgnoreCase(subscriberDTO.getEmail()).isPresent()) {
			// throw new EmailAlreadyUsedException();
		} else {
			Subscriber newSubscriber = subscriberService.createSubscriber(subscriberDTO);

			log.debug("Subscriber saved: {}", newSubscriber);

			mailService.sendSubscribeEmail(newSubscriber);
			mailService.sendSubscribeEmailToAdmin(newSubscriber);

			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
	}

	@GetMapping("/subscribers")
	@Timed
	public ResponseEntity<List<SubscriberDTO>> getAllSubscribers(Pageable pageable) {
		final Page<SubscriberDTO> page = subscriberService.getAllSubscribers(pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/subscribers");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	@DeleteMapping("/subscribers/{id}")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<Void> deleteSubscriber(@PathVariable Long id) {
		log.debug("REST request to delete a Subscriber: {}", id);
		subscriberService.deleteSubscriber(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("subscriber", id.toString())).build();
	}
}
