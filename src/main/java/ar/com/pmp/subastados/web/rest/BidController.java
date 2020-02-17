package ar.com.pmp.subastados.web.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ar.com.pmp.subastados.domain.Bid;
import ar.com.pmp.subastados.domain.Lote;
import ar.com.pmp.subastados.repository.EventRepository;
import ar.com.pmp.subastados.repository.LoteRepository;
import ar.com.pmp.subastados.security.AuthoritiesConstants;
import ar.com.pmp.subastados.service.BidService;
import ar.com.pmp.subastados.service.dto.BidDTO;
import ar.com.pmp.subastados.web.rest.errors.EntityNotFoundException;
import ar.com.pmp.subastados.web.rest.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class BidController {

	private final Logger log = LoggerFactory.getLogger(BidController.class);

	@Autowired
	private BidService bidService;
	@Autowired
	private LoteRepository loteRepository;

	@Autowired
	private CacheManager cacheManager;

	@PostMapping("/bid/")
	@ResponseStatus(code = HttpStatus.OK)
	public BidDTO createBid(@RequestBody BidDTO bidDTO) throws Exception {
		log.debug("Create new Bid: [{}]", bidDTO);

		BidDTO bidCreated = bidService.createBid(bidDTO);

		// Eliminar evento de cache
		cacheManager.getCache(EventRepository.EVENT_BY_ID_CACHE).evict(bidCreated.getEventId());

		return bidCreated;
	}

	@GetMapping("/bid/{loteId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<List<BidDTO>> findByLoteId(@PathVariable("loteId") Long loteId, Pageable pageable) throws Exception {
		log.debug("Get bids by loteId {}", loteId);

		final Page<BidDTO> page = bidService.findByLoteId(loteId, pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bid/" + loteId);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	@DeleteMapping("/bid/{loteId}/deleteLastBid")
	@Secured(AuthoritiesConstants.ADMIN)
	@ResponseStatus(code = HttpStatus.OK)
	public void deleteBid(@PathVariable("loteId") Long loteId) throws Exception {
		log.debug("Delete last bid for loteId: [{}]", loteId);

		Lote lote = loteRepository.findOne(loteId);
		if (lote == null) {
			throw new EntityNotFoundException("Debe seleccionar un lote valido");
		}

		// Solo se puede borrar la ultima
		Bid lastBid = lote.getLastBid();
		if (lastBid != null) {

			bidService.delete(lote);

			// Eliminar evento de cache
			cacheManager.getCache(EventRepository.EVENT_BY_ID_CACHE).evict(lote.getEvent().getId());
		} else {
			throw new EntityNotFoundException("El lote seleccionado no tiene ofertas");
		}
	}
}
