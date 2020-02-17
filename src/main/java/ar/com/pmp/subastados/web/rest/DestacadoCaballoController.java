package ar.com.pmp.subastados.web.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ar.com.pmp.subastados.service.DestacadoCaballoService;
import ar.com.pmp.subastados.service.dto.DestacadoCaballoDTO;

@RestController
@RequestMapping("/api/destacadoCaballo")
public class DestacadoCaballoController {

	private final Logger log = LoggerFactory.getLogger(DestacadoCaballoController.class);

	@Autowired
	private DestacadoCaballoService destacadoCaballoService;

	@GetMapping("/all")
	@ResponseStatus(code = HttpStatus.OK)
	public List<DestacadoCaballoDTO> getAll() {
		log.debug("Return all destacado info");
		List<DestacadoCaballoDTO> list = destacadoCaballoService.getAll();
		log.debug("Returning: ", list.toString());
		return list;
	}

	// @GetMapping("/refresh")
	// @ResponseStatus(code = HttpStatus.OK)
	// public void refresh() {
	// log.debug("Refresh all destacado info");
	// destacadoCaballoService.sincronizar();
	// }
}
