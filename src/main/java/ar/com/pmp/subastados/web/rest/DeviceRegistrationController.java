package ar.com.pmp.subastados.web.rest;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ar.com.pmp.subastados.domain.DeviceRegistration;
import ar.com.pmp.subastados.security.SecurityUtils;
import ar.com.pmp.subastados.service.DeviceRegistrationService;
import ar.com.pmp.subastados.service.dto.DeviceRegistrationDTO;

@RestController
@RequestMapping("/api")
public class DeviceRegistrationController {

	private final Logger log = LoggerFactory.getLogger(DeviceRegistrationController.class);

	@Autowired
	private DeviceRegistrationService deviceRegistrationService;

	// XXX asi como esta este controller se puede llamar siempre y cuando el usuario este logueado, eso esta bien?

	@PostMapping("/deviceRegistration/")
	public ResponseEntity<Void> createEvent(@RequestBody DeviceRegistrationDTO deviceRegistrationDTO) throws Exception {
		log.info("Create new device registration: [{}]", deviceRegistrationDTO);

		deviceRegistrationService.create(deviceRegistrationDTO);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/deviceRegistration/")
	@ResponseStatus(code = HttpStatus.OK)
	public Collection<DeviceRegistrationDTO> findByUserLogin() throws Exception {
		log.info("Get device registration by user");
		Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
		Set<DeviceRegistration> collection = deviceRegistrationService.findByUserLogin(userLogin.get());
		return collection.stream().map(DeviceRegistrationDTO::new).collect(Collectors.toList());
	}
}
