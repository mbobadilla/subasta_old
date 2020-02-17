package ar.com.pmp.subastados.web.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import ar.com.pmp.subastados.domain.Event;
import ar.com.pmp.subastados.domain.PersistentToken;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.repository.EventRepository;
import ar.com.pmp.subastados.repository.PersistentTokenRepository;
import ar.com.pmp.subastados.repository.UserRepository;
import ar.com.pmp.subastados.security.SecurityUtils;
import ar.com.pmp.subastados.service.EventParticipantService;
import ar.com.pmp.subastados.service.GeneralNotificationService;
import ar.com.pmp.subastados.service.MailService;
import ar.com.pmp.subastados.service.UserService;
import ar.com.pmp.subastados.service.WhatsappService;
import ar.com.pmp.subastados.service.dto.UserDTO;
import ar.com.pmp.subastados.web.rest.errors.BadRequestException;
import ar.com.pmp.subastados.web.rest.errors.EmailAlreadyUsedException;
import ar.com.pmp.subastados.web.rest.errors.EmailNotFoundException;
import ar.com.pmp.subastados.web.rest.errors.InternalServerErrorException;
import ar.com.pmp.subastados.web.rest.errors.InvalidPasswordException;
import ar.com.pmp.subastados.web.rest.errors.LoginAlreadyUsedException;
import ar.com.pmp.subastados.web.rest.vm.KeyAndPasswordVM;
import ar.com.pmp.subastados.web.rest.vm.ManagedUserVM;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

	private final Logger log = LoggerFactory.getLogger(AccountResource.class);

	private final UserRepository userRepository;

	private final UserService userService;

	private final MailService mailService;

	private final PersistentTokenRepository persistentTokenRepository;

	@Value("${global.application-public-url}")
	private String applicationPublicUrl;

	@Autowired
	private WhatsappService whatsappService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private GeneralNotificationService notificationService;
	@Autowired
	private EventParticipantService eventParticipantService;

	@Autowired
	private EventRepository eventRepository;

	public AccountResource(UserRepository userRepository, UserService userService, MailService mailService,
			PersistentTokenRepository persistentTokenRepository) {

		this.userRepository = userRepository;
		this.userService = userService;
		this.mailService = mailService;
		this.persistentTokenRepository = persistentTokenRepository;
	}

	/**
	 * POST /register : register the user.
	 * @param managedUserVM
	 * the managed user View Model
	 * @throws InvalidPasswordException
	 * 400 (Bad Request) if the password is incorrect
	 * @throws EmailAlreadyUsedException
	 * 400 (Bad Request) if the email is already used
	 * @throws LoginAlreadyUsedException
	 * 400 (Bad Request) if the login is already used
	 */
	@PostMapping("/register")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	@Deprecated
	public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
		if (!checkPasswordLength(managedUserVM.getPassword())) {
			throw new InvalidPasswordException();
		}
		userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase()).ifPresent(u -> {
			throw new LoginAlreadyUsedException();
		});
		userRepository.findOneByEmailIgnoreCase(managedUserVM.getEmail()).ifPresent(u -> {
			throw new EmailAlreadyUsedException();
		});

		User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());

		mailService.sendActivationEmail(user);

		if (user.isPhoneNotifications()) {
			Locale locale = Locale.forLanguageTag(user.getLangKey());
			String[] args = new String[] { user.getPhoneActivationKey() };
			String message = messageSource.getMessage("notification.whatsapp.validationMessage", args, locale);
			whatsappService.sendMessage(user.getCellPhone(), message);

			log.info("Enviando codigo de validacion de whatsapp");
		}

		notificationService.sendNewUserNotification(user);
	}

	@PostMapping("/register/firstStep")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public void registerUserFirstStep(@Valid @RequestBody ManagedUserVM managedUserVM) {
		if (!checkPasswordLength(managedUserVM.getPassword())) {
			throw new InvalidPasswordException();
		}
		userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase()).ifPresent(u -> {
			throw new LoginAlreadyUsedException();
		});
		userRepository.findOneByEmailIgnoreCase(managedUserVM.getEmail()).ifPresent(u -> {
			throw new EmailAlreadyUsedException();
		});

		User user = userService.registerUserFirstStep(managedUserVM, managedUserVM.getPassword());

		// Enviando codigo de validacion de mail
		mailService.sendActivationEmail(user);

		// Enviando codigo de validacion de whatsapp
		Locale locale = Locale.forLanguageTag(user.getLangKey());
		String[] args = new String[] { user.getPhoneActivationKey() };
		String message = messageSource.getMessage("notification.whatsapp.validationMessage", args, locale);
		whatsappService.sendMessage(user.getCellPhone(), message);
		log.info("Enviando codigo de validacion de whatsapp");

		notificationService.sendNewUserNotification(user);

		try {
			eventParticipantService.participateToLastEvent(user.getLogin());
		} catch (Throwable e) {
			// No hacer nada
		}
	}

	@PostMapping("/register/secondStep")
	@Timed
	@ResponseStatus(HttpStatus.CREATED)
	public void registerUserSecondStep(@Valid @RequestBody ManagedUserVM managedUserVM) {
		User user = userService.registerUserSecondStep(managedUserVM);
		if (!user.isPhoneValid()) {
			throw new BadRequestException("Bad validation key");
		} else {
			if (user.isPhoneNotifications()) {
				Locale locale = Locale.forLanguageTag(user.getLangKey());
				String message = messageSource.getMessage("notification.whatsapp.validationSuccessMessage", null, locale);
				whatsappService.sendMessage(user.getCellPhone(), message);
			}
		}
	}

	/**
	 * GET /validatePhone : validate user phone
	 * @param key
	 * the validation key
	 * @throws RuntimeException
	 * 500 (Internal Server Error) if the user's phone couldn't be
	 * validated
	 */
	@GetMapping("/validatePhone")
	@Timed
	public void phoneValidation(@RequestParam(value = "login") String login, @RequestParam(value = "key") String key) {
		Optional<User> optional = userService.phoneValidation(login, key);
		if (!optional.isPresent()) {
			throw new InternalServerErrorException("No user was found for this validation key");
		} else {
			User user = optional.get();
			if (user.isPhoneNotifications() && user.isPhoneValid()) {
				Locale locale = Locale.forLanguageTag(user.getLangKey());
				String message = messageSource.getMessage("notification.whatsapp.validationSuccessMessage", null, locale);
				whatsappService.sendMessage(user.getCellPhone(), message);
			}
		}
	}

	/**
	 * GET /validateMail : validate the registered user's mail.
	 * @param key
	 * the validation key
	 * @throws RuntimeException
	 * 500 (Internal Server Error) if the user couldn't be activated
	 */
	@GetMapping("/validateMail")
	@Timed
	public void validateMail(@RequestParam(value = "key") String key) {
		Optional<User> user = userService.validateMail(key);
		if (!user.isPresent()) {
			throw new InternalServerErrorException("No user was found for this validation key");
		}
	}

	/**
	 * GET /authenticate : check if the user is authenticated, and return its login.
	 * @param request
	 * the HTTP request
	 * @return the login if the user is authenticated
	 */
	@GetMapping("/authenticate")
	@Timed
	public String isAuthenticated(HttpServletRequest request) {
		log.debug("REST request to check if the current user is authenticated");
		return request.getRemoteUser();
	}

	/**
	 * GET /account : get the current user.
	 * @return the current user
	 * @throws RuntimeException
	 * 500 (Internal Server Error) if the user couldn't be returned
	 */
	@GetMapping("/account")
	@Timed
	public UserDTO getAccount() {

		User user = userService.getUserWithAuthorities().orElseThrow(() -> new InternalServerErrorException("User could not be found"));
		UserDTO userDTO = new UserDTO(user);

		Long lastEventId = eventRepository.getMaxId();
		if (lastEventId != null) {
			Event lastEvent = eventRepository.findOne(lastEventId);
			if (lastEvent.isActive()) {
				userDTO.setLastEvent(lastEvent.getName());
				userDTO.setLastEventParticipant(user.isParticipant(lastEventId));
			}
		}

		return userDTO;
	}

	/**
	 * POST /account : update the current user information by the user himself.
	 * @param userDTO
	 * the current user information
	 * @throws EmailAlreadyUsedException
	 * 400 (Bad Request) if the email is already used
	 * @throws RuntimeException
	 * 500 (Internal Server Error) if the user login wasn't found
	 */
	@PostMapping("/account")
	@Timed
	public void saveAccount(@Valid @RequestBody UserDTO userDTO) {
		final String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("Current user login not found"));
		Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
		if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
			throw new EmailAlreadyUsedException();
		}
		Optional<User> userOptional = userRepository.findOneByLogin(userLogin);
		if (!userOptional.isPresent()) {
			throw new InternalServerErrorException("User could not be found");
		}

		userService.updateDataByUser(userDTO);

		userOptional = userRepository.findOneByLogin(userLogin);
		userOptional.ifPresent(user -> {
			if (user.isEmailNotifications() && !user.isEmailValid()) {
				// TODO Crear notificacion de usuario, mail invalido
				mailService.sendActivationEmail(user);
			}

			if (user.isPhoneNotifications() && !user.isPhoneValid()) {
				// TODO Crear notificacion de usuario, telefono invalido

				// Locale locale = Locale.forLanguageTag(user.getLangKey());
				// String[] args = new String[] { user.getPhoneActivationKey() };
				// String message =
				// messageSource.getMessage("notification.whatsapp.validationMessage", args,
				// locale);
				// whatsappService.sendMessage(user.getCellPhone(), message);
				// log.info("Enviando codigo de validacion de whatsapp");
			}
		});
	}

	@GetMapping("/reSendValidationEmail")
	public ResponseEntity<Void> reSendValidationEmail() {
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("User could not be found"))).get();
		if (user != null && !user.isEmailValid()) {
			user = userService.generateEmailActivationKey(user);
			mailService.sendActivationEmail(user);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.badRequest().build();
	}

	@GetMapping("/reSendValidationPhone")
	public ResponseEntity<Void> reSendValidationPhone() {
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("User could not be found"))).get();
		if (user != null && !user.isPhoneValid()) {
			user = userService.generatePhoneActivationKey(user);
			Locale locale = Locale.forLanguageTag(user.getLangKey());
			String[] args = new String[] { user.getPhoneActivationKey() };
			String message = messageSource.getMessage("notification.whatsapp.validationMessage", args, locale);
			whatsappService.sendMessage(user.getCellPhone(), message);
			log.info("Enviando codigo de validacion de whatsapp");
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.badRequest().build();
	}

	/**
	 * POST /account/change-password : changes the current user's password
	 * @param password
	 * the new password
	 * @throws InvalidPasswordException
	 * 400 (Bad Request) if the new password is incorrect
	 */
	@PostMapping(path = "/account/change-password")
	@Timed
	public void changePassword(@RequestBody String password) {
		if (!checkPasswordLength(password)) {
			throw new InvalidPasswordException();
		}
		userService.changePassword(password);
	}

	/**
	 * GET /account/sessions : get the current open sessions.
	 * @return the current open sessions
	 * @throws RuntimeException
	 * 500 (Internal Server Error) if the current open sessions couldn't
	 * be retrieved
	 */
	@GetMapping("/account/sessions")
	@Timed
	public List<PersistentToken> getCurrentSessions() {
		return persistentTokenRepository.findByUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("Current user login not found"))).orElseThrow(() -> new InternalServerErrorException("User could not be found")));
	}

	/**
	 * DELETE /account/sessions?series={series} : invalidate an existing session. -
	 * You can only delete your own sessions, not any other user's session - If you
	 * delete one of your existing sessions, and that you are currently logged in on
	 * that session, you will still be able to use that session, until you quit your
	 * browser: it does not work in real time (there is no API for that), it only
	 * removes the "remember me" cookie - This is also true if you invalidate your
	 * current session: you will still be able to use it until you close your
	 * browser or that the session times out. But automatic login (the "remember me"
	 * cookie) will not work anymore. There is an API to invalidate the current
	 * session, but there is no API to check which session uses which cookie.
	 * @param series
	 * the series of an existing session
	 * @throws UnsupportedEncodingException
	 * if the series couldnt be URL decoded
	 */
	@DeleteMapping("/account/sessions/{series}")
	@Timed
	public void invalidateSession(@PathVariable String series) throws UnsupportedEncodingException {
		String decodedSeries = URLDecoder.decode(series, "UTF-8");
		SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(u -> persistentTokenRepository.findByUser(u).stream().filter(persistentToken -> StringUtils.equals(persistentToken.getSeries(), decodedSeries)).findAny().ifPresent(t -> persistentTokenRepository.delete(decodedSeries)));
	}

	/**
	 * POST /account/reset-password/init : Send an email to reset the password of
	 * the user
	 * @param mail
	 * the mail of the user
	 * @throws EmailNotFoundException
	 * 400 (Bad Request) if the email address is not registered
	 */
	@PostMapping(path = "/account/reset-password/init")
	@Timed
	public void requestPasswordReset(@RequestBody String mail) {
		mailService.sendPasswordResetMail(userService.requestPasswordReset(mail).orElseThrow(EmailNotFoundException::new));
	}

	/**
	 * POST /account/reset-password/finish : Finish to reset the password of the
	 * user
	 * @param keyAndPassword
	 * the generated key and the new password
	 * @throws InvalidPasswordException
	 * 400 (Bad Request) if the password is incorrect
	 * @throws RuntimeException
	 * 500 (Internal Server Error) if the password could not be reset
	 */
	@PostMapping(path = "/account/reset-password/finish")
	@Timed
	public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
		if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
			throw new InvalidPasswordException();
		}
		Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

		if (!user.isPresent()) {
			throw new InternalServerErrorException("No user was found for this reset key");
		}
	}

	private static boolean checkPasswordLength(String password) {
		return !StringUtils.isEmpty(password) && password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH && password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
	}
}
