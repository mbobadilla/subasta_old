package ar.com.pmp.subastados.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.pmp.subastados.domain.NotificationType;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.domain.UserNotification;
import ar.com.pmp.subastados.repository.UserNotificationRepository;
import ar.com.pmp.subastados.repository.UserRepository;
import ar.com.pmp.subastados.security.SecurityUtils;
import ar.com.pmp.subastados.web.rest.errors.EntityNotFoundException;

@Service
@Transactional
public class UserNotificationService {

	private final Logger log = LoggerFactory.getLogger(UserNotificationService.class);

	@Autowired
	private UserNotificationRepository userNotificationRepository;
	@Autowired
	private UserRepository userRepository;

	@Transactional
	public UserNotification create(User user, NotificationType type) {
		log.info("Creando nueva notifiacion de usuario");

		UserNotification userNotification = new UserNotification();
		userNotification.setType(type);
		userNotification.setUser(user);
		userNotification.setMessage(type.getMessage());

		return userNotification;
	}

	@Transactional
	public List<UserNotification> findByUserLogin() {
		Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
		Optional<User> optional = userRepository.findOneByLogin(userLogin.get());
		User user = optional.orElseThrow(() -> new EntityNotFoundException("User not found"));
		return userNotificationRepository.findByUser(user);
	}

	@Transactional
	public void removeByUserAndType(User user, NotificationType type) {
		userNotificationRepository.findByUserAndType(user, type).ifPresent(list -> {
			userNotificationRepository.delete(list);
		});
	}
}
