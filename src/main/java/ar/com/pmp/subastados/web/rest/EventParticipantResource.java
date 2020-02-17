package ar.com.pmp.subastados.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.com.pmp.subastados.service.EventParticipantService;

@RestController
@RequestMapping("/api")
public class EventParticipantResource {

	@Autowired
	private EventParticipantService eventParticipantService;

	@GetMapping("/participate/{login}")
	public ResponseEntity<Void> participate(@PathVariable(name = "login") String login) {
		eventParticipantService.participateToLastEvent(login);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
