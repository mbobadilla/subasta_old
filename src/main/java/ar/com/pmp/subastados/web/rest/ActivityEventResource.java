package ar.com.pmp.subastados.web.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.com.pmp.subastados.config.Constants;
import ar.com.pmp.subastados.domain.ActivityEventType;
import ar.com.pmp.subastados.security.AuthoritiesConstants;
import ar.com.pmp.subastados.security.SecurityUtils;
import ar.com.pmp.subastados.service.ActivityEventService;
import ar.com.pmp.subastados.service.dto.ActivityEventDTO;
import ar.com.pmp.subastados.web.rest.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class ActivityEventResource {

	@Autowired
	private ActivityEventService activityEventService;

	@GetMapping("/activityEvent/productView/{loteId}")
	public ResponseEntity<Void> productView(@PathVariable(name = "loteId") Long loteId, HttpServletRequest request) {

		String login = SecurityUtils.getCurrentUserLogin().orElse(Constants.ANONYMOUS_USER);
		String extra = "";
		if (Constants.ANONYMOUS_USER.equalsIgnoreCase(login)) {
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
			}
			extra = ipAddress;
		}

		activityEventService.createProductView(login, extra, loteId);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("/activityEvent/{login}")
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<List<ActivityEventDTO>> getUserEvents(@PathVariable String login, @RequestParam(name = "types", required = false) ActivityEventType[] types, 
			@PageableDefault(size = 20)Pageable pageable) {
		types = types == null || types.length == 0 ? ActivityEventType.values() : types;
		final Page<ActivityEventDTO> page = activityEventService.findByLoginAndType(login, types, pageable);

		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/activityEvent/" + login);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}
}
