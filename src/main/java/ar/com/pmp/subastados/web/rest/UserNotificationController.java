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

import ar.com.pmp.subastados.domain.UserNotification;
import ar.com.pmp.subastados.service.UserNotificationService;

@RestController
@RequestMapping("/api")
public class UserNotificationController {

	private final Logger log = LoggerFactory.getLogger(UserNotificationController.class);

	@Autowired
	private UserNotificationService userNotificationService;

	@GetMapping("/userNotification/")
	@ResponseStatus(code = HttpStatus.OK)
	public List<UserNotification> findByUserLogin() throws Exception {
		log.info("Get device notifications by user");
		List<UserNotification> list = userNotificationService.findByUserLogin();
		log.info("Returning user notifications: [{}]", list);
		return list;
	}
}
