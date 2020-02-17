package ar.com.pmp.subastados.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ar.com.pmp.subastados.security.AuthoritiesConstants;
import ar.com.pmp.subastados.service.LoteService;
import ar.com.pmp.subastados.service.dto.LoteDTO;

@RestController
@RequestMapping("/api")
public class LoteController {

	private final Logger log = LoggerFactory.getLogger(LoteController.class);

	@Autowired
	private LoteService loteService;

	@GetMapping("/lote/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public LoteDTO createEvent(@PathVariable("id") Long id) throws Exception {
		log.debug("Get lote: {}", id);

		LoteDTO loteDTO = loteService.findById(id);

		log.debug("Returning loteDTO: {}", loteDTO);
		return loteDTO;
	}

	@GetMapping("/lote/{id}/follower/add")
	@ResponseStatus(code = HttpStatus.OK)
	public void addFollower(@PathVariable("id") Long id) throws Exception {
		log.debug("Add follower to lote: {}", id);
		loteService.addFollower(id);
	}

	@GetMapping("/lote/{id}/follower/remove")
	@ResponseStatus(code = HttpStatus.OK)
	public void removeFollower(@PathVariable("id") Long id) throws Exception {
		log.debug("Remove follower from lote: {}", id);
		loteService.removeFollower(id);
	}

	@Secured(AuthoritiesConstants.ADMIN)
	@GetMapping("/lote/{id}/postpone")
	@ResponseStatus(code = HttpStatus.OK)
	public void postponeLoteEndDate(@PathVariable("id") Long id, @RequestParam("minutes") Long minutes) throws Exception {
		log.debug("Postpone lote end date: {} for {} minutes", id, minutes);
		loteService.postponeLoteEndDate(id, minutes);
	}
}
