package ar.com.pmp.subastados.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.com.pmp.subastados.domain.MobilePlatform;
import ar.com.pmp.subastados.domain.MobileVersion;
import ar.com.pmp.subastados.service.MobileVersionService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class MobileVersionController {

	private final Logger log = LoggerFactory.getLogger(MobileVersionController.class);

	@Autowired
	private MobileVersionService service;

	@RequestMapping(path = "/mobileVersion/isValid", method = RequestMethod.GET)
	public ResponseEntity<Void> isValid(@RequestParam("platform") MobilePlatform platform, @RequestParam("version") String version) {
		log.info("MobileVersion is valid: {} - {}", platform, version);
		MobileVersion mobileVersion = service.findOneByPlatformAndVersion(platform.name(), version);
		log.info("Version encontrada: {}", mobileVersion);
		if (mobileVersion.isActive())
			return ResponseEntity.ok().build();
		return ResponseEntity.badRequest().build();
	}

	@PostMapping("/mobileVersion/")
	public ResponseEntity<Void> createMobileVersion(@RequestParam("platform") MobilePlatform platform, @RequestParam("version") String version) {
		log.info("Create new MobileVersion: {} - {}", platform, version);
		service.create(platform.name(), version);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/mobileVersion/")
	@ApiOperation(value = "Deprecates the mobile version")
	public ResponseEntity<Void> deprecateMobileVersion(@RequestParam("platform") MobilePlatform platform, @RequestParam("version") String version) {
		log.info("Deprecate MobileVersion: {} - {}", platform, version);
		service.deprecate(platform.name(), version);
		return ResponseEntity.ok().build();
	}
}
