package ar.com.pmp.subastados.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.pmp.subastados.config.Constants;
import ar.com.pmp.subastados.domain.Authority;
import ar.com.pmp.subastados.domain.GeneralConfiguration;
import ar.com.pmp.subastados.domain.GeneralConfigurationKey;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.repository.AuthorityRepository;
import ar.com.pmp.subastados.repository.GeneralConfigurationRepository;
import ar.com.pmp.subastados.repository.PersistentTokenRepository;
import ar.com.pmp.subastados.repository.UserRepository;
import ar.com.pmp.subastados.security.AuthoritiesConstants;
import ar.com.pmp.subastados.security.SecurityUtils;
import ar.com.pmp.subastados.service.dto.FilterUserDTO;
import ar.com.pmp.subastados.service.dto.UserDTO;
import ar.com.pmp.subastados.service.util.RandomUtil;
import ar.com.pmp.subastados.web.rest.errors.LoginNotFoundException;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

	private final Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final SocialService socialService;

	private final PersistentTokenRepository persistentTokenRepository;

	private final AuthorityRepository authorityRepository;

	private final CacheManager cacheManager;

	@Autowired
	private GeneralConfigurationRepository generalConfigurationRepository;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SocialService socialService, PersistentTokenRepository persistentTokenRepository,
			AuthorityRepository authorityRepository, CacheManager cacheManager) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.socialService = socialService;
		this.persistentTokenRepository = persistentTokenRepository;
		this.authorityRepository = authorityRepository;
		this.cacheManager = cacheManager;
	}

	public Optional<User> phoneValidation(String login, String key) {
		log.debug("Validating user's phone for user: {} and key {}", login, key);
		return userRepository.findOneByLoginAndPhoneActivationKey(login, key).map(user -> {
			// validate given user's phone for the key.
			user.setPhoneValid(true);
			user.setPhoneActivationKey(null);
			cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
			cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
			log.debug("Phone validated for user: {}", user);
			return user;
		});
	}

	public Optional<User> validateMail(String key) {
		log.debug("Validating user for activation key {}", key);
		return userRepository.findOneByActivationKey(key).map(user -> {
			// activate given user for the registration key.
			user.setEmailValid(true);
			user.setActivationKey(null);
			cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
			cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
			log.debug("User's mail validated: {}", user);
			return user;
		});
	}

	public Optional<User> completePasswordReset(String newPassword, String key) {
		log.debug("Reset user password for reset key {}", key);

		return userRepository.findOneByResetKey(key).filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400))).map(user -> {
			user.setPassword(passwordEncoder.encode(newPassword));
			user.setResetKey(null);
			user.setResetDate(null);
			cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
			cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
			return user;
		});
	}

	public Optional<User> requestPasswordReset(String mail) {
		return userRepository.findOneByEmailIgnoreCase(mail).filter(User::getActivated).map(user -> {
			user.setResetKey(RandomUtil.generateResetKey());
			user.setResetDate(Instant.now());
			cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
			cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
			return user;
		});
	}

	/**
	 * Registracion de un usuario por solicitud propia
	 * @param userDTO
	 * @param password
	 * @return
	 */
	@Deprecated
	public User registerUser(UserDTO userDTO, String password) {

		User newUser = new User();
		Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
		Set<Authority> authorities = new HashSet<>();
		String encryptedPassword = passwordEncoder.encode(password);
		newUser.setLogin(userDTO.getLogin());
		// new user gets initially a generated password
		newUser.setPassword(encryptedPassword);
		newUser.setFirstName(userDTO.getFirstName());
		newUser.setLastName(userDTO.getLastName());
		newUser.setEmail(userDTO.getEmail());
		newUser.setImageUrl(userDTO.getImageUrl());
		newUser.setLangKey(userDTO.getLangKey());
		// new user is active
		newUser.setActivated(true);
		// new user gets registration key
		newUser.setActivationKey(RandomUtil.generateActivationKey());
		authorities.add(authority);
		newUser.setAuthorities(authorities);
		newUser.setDni(userDTO.getDni());
		newUser.setCellPhone(userDTO.getCellPhone());
		newUser.setAddress(userDTO.getAddress());
		newUser.setBirthdayDate(userDTO.getBirthday());
		newUser.setAcceptedConditions(userDTO.isAcceptedConditions());

		newUser.setEmailNotifications(userDTO.isEmailNotifications());
		newUser.setPhoneNotifications(userDTO.isPhoneNotifications());
		newUser.setPhoneActivationKey(RandomUtil.generateWhatsappActivationKey());
		newUser.setPhoneValid(false);
		newUser.setPresentedBy(userDTO.getPresentedBy());
		newUser.setPostalCode(userDTO.getPostalCode());
		newUser.setCity(userDTO.getCity());
		newUser.setUserCountry(userDTO.getUserCountry());

		userRepository.save(newUser);
		cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(newUser.getLogin());
		cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(newUser.getEmail());
		log.debug("Created Information for User: {}", newUser);
		return newUser;
	}

	/**
	 * Registracion de un usuario por solicitud propia (PRIMER PASO)
	 * @param userDTO
	 * @param password
	 * @return
	 */
	public User registerUserFirstStep(UserDTO userDTO, String password) {

		User newUser = new User();

		Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
		Set<Authority> authorities = new HashSet<>();
		authorities.add(authority);
		newUser.setAuthorities(authorities);

		String encryptedPassword = passwordEncoder.encode(password);

		newUser.setLogin(userDTO.getLogin());
		newUser.setPassword(encryptedPassword);
		newUser.setFirstName(userDTO.getFirstName());
		newUser.setLastName(userDTO.getLastName());
		newUser.setEmail(userDTO.getEmail());
		newUser.setCellPhone(userDTO.getCellPhone());
		newUser.setAcceptedConditions(userDTO.isAcceptedConditions());
		newUser.setLangKey(userDTO.getLangKey());

		GeneralConfiguration validatedByAdminConfig = generalConfigurationRepository.findOne(GeneralConfigurationKey.USER_VALIDATED_BY_ADMIN.name());
		Boolean validatedByAdmin = Optional.ofNullable(validatedByAdminConfig).map(x -> BooleanUtils.toBoolean(x.getValue())).orElse(false);

		if(validatedByAdmin) {
			// Lo valida a mano el administrador
			newUser.setActivated(false);
		} else {
			// Auto-validado
			newUser.setActivated(true);
		}

		newUser.setPhoneValid(false);
		newUser.setEmailValid(false);
		newUser.setActivationKey(RandomUtil.generateActivationKey());
		newUser.setPhoneActivationKey(RandomUtil.generateWhatsappActivationKey());
		newUser.setPresentedBy(null);

		userRepository.save(newUser);
		cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(newUser.getLogin());
		cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(newUser.getEmail());
		log.debug("Created Information for User (first step): {}", newUser);
		return newUser;
	}

	/**
	 * Registracion de un usuario por solicitud propia (SEGUNDO PASO)
	 * @param userDTO
	 * @param password
	 * @return
	 */
	public User registerUserSecondStep(UserDTO userDTO) {

		User user = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).orElseThrow(() -> new LoginNotFoundException());

		user.setDni(userDTO.getDni());
		user.setBirthdayDate(userDTO.getBirthday());
		user.setAddress(userDTO.getAddress());
		user.setPostalCode(userDTO.getPostalCode());
		user.setCity(userDTO.getCity());
		user.setUserCountry(userDTO.getUserCountry());
		user.setEmailNotifications(userDTO.isEmailNotifications());
		user.setPhoneNotifications(userDTO.isPhoneNotifications());

		if (StringUtils.equalsIgnoreCase(user.getPhoneActivationKey(), userDTO.getPhoneValidationKey())) {
			user.setPhoneValid(true);
			user.setPhoneActivationKey(null);
			log.debug("Phone validated for user: {}", user);
		}

		cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
		cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
		log.debug("Created Information for User (second step): {}", user);
		return user;
	}

	public User generatePhoneActivationKey(User user) {
		user.setPhoneActivationKey(RandomUtil.generateWhatsappActivationKey());
		user.setPhoneValid(false);
		userRepository.save(user);
		cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
		cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
		return user;
	}

	public User generateEmailActivationKey(User user) {
		user.setActivationKey(RandomUtil.generateActivationKey());
		user.setEmailValid(false);
		userRepository.save(user);
		cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
		cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
		return user;
	}

	/**
	 * Creacion de un usuario por Admin
	 * @param userDTO
	 * @return
	 */
	public User createUser(UserDTO userDTO) {
		User user = new User();
		user.setLogin(userDTO.getLogin());
		String encryptedPassword = passwordEncoder.encode(userDTO.getLogin());
		user.setPassword(encryptedPassword); // pass == user
		user.setFirstLogin(true);

		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setEmail(userDTO.getEmail());
		user.setImageUrl(userDTO.getImageUrl());
		if (userDTO.getLangKey() == null) {
			user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
		} else {
			user.setLangKey(userDTO.getLangKey());
		}
		if (userDTO.getAuthorities() != null) {
			Set<Authority> authorities = userDTO.getAuthorities().stream().map(authorityRepository::findOne).collect(Collectors.toSet());
			user.setAuthorities(authorities);
		}
		user.setResetKey(RandomUtil.generateResetKey());
		user.setResetDate(Instant.now());
		user.setActivated(true);
		user.setDni(userDTO.getDni());
		user.setCellPhone(userDTO.getCellPhone());
		user.setAddress(userDTO.getAddress());
		user.setBirthdayDate(userDTO.getBirthday());
		user.setAcceptedConditions(userDTO.isAcceptedConditions());
		user.setEmailNotifications(userDTO.isEmailNotifications());
		user.setEmailValid(userDTO.isEmailValid());
		user.setPhoneNotifications(userDTO.isPhoneNotifications());
		user.setPhoneValid(userDTO.isPhoneValid());
		user.setPresentedBy(userDTO.getPresentedBy());
		user.setPostalCode(userDTO.getPostalCode());
		user.setCity(userDTO.getCity());
		user.setUserCountry(userDTO.getUserCountry());
		user.setComments(userDTO.getComments());
		userRepository.save(user);

		cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
		cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
		log.debug("Created Information for User: {}", user);
		return user;
	}

	/**
	 * Update basic information for the current user (by the user).
	 * @param UserDTO userDTO
	 */
	public void updateDataByUser(UserDTO userDTO) {
		SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(user -> {
			user.setFirstName(userDTO.getFirstName());
			user.setLastName(userDTO.getLastName());
			user.setLangKey(userDTO.getLangKey());
			user.setImageUrl(userDTO.getImageUrl());
			user.setDni(userDTO.getDni());
			user.setAddress(userDTO.getAddress());
			user.setBirthdayDate(userDTO.getBirthday());

			if (user.getEmail() != null && !user.getEmail().equals(userDTO.getEmail())) {
				user.setEmailValid(false);
				user.setActivationKey(RandomUtil.generateActivationKey());
			}
			user.setEmail(userDTO.getEmail());

			if (user.getCellPhone() != null && !user.getCellPhone().equals(userDTO.getCellPhone())) {
				user.setPhoneValid(false);
			}
			user.setCellPhone(userDTO.getCellPhone());

			user.setEmailNotifications(userDTO.isEmailNotifications());
			user.setPhoneNotifications(userDTO.isPhoneNotifications());

			/////

			cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
			cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
			log.debug("Changed Information for User: {}", user);
		});
	}

	/**
	 * Update all information for a specific user, and return the modified user. (By Admin)
	 * @param userDTO user to update
	 * @return updated user
	 */
	public Optional<UserDTO> updateUser(UserDTO userDTO) {
		return Optional.of(userRepository.findOne(userDTO.getId())).map(user -> {
			user.setLogin(userDTO.getLogin());
			user.setFirstName(userDTO.getFirstName());
			user.setLastName(userDTO.getLastName());
			user.setEmail(userDTO.getEmail());
			user.setImageUrl(userDTO.getImageUrl());
			user.setActivated(userDTO.isActivated());
			user.setLangKey(userDTO.getLangKey());
			user.setDni(userDTO.getDni());
			user.setCellPhone(userDTO.getCellPhone());
			user.setAddress(userDTO.getAddress());
			user.setBirthdayDate(userDTO.getBirthday());
			user.setAcceptedConditions(userDTO.isAcceptedConditions());
			user.setEmailNotifications(userDTO.isEmailNotifications());
			user.setPhoneNotifications(userDTO.isPhoneNotifications());
			user.setPhoneValid(userDTO.isPhoneValid());
			user.setEmailValid(userDTO.isEmailValid());
			user.setPresentedBy(userDTO.getPresentedBy());
			user.setPostalCode(userDTO.getPostalCode());
			user.setCity(userDTO.getCity());
			user.setUserCountry(userDTO.getUserCountry());
			user.setComments(userDTO.getComments());

			Set<Authority> managedAuthorities = user.getAuthorities();
			managedAuthorities.clear();
			userDTO.getAuthorities().stream().map(authorityRepository::findOne).forEach(managedAuthorities::add);
			cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
			cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
			log.debug("Changed Information for User: {}", user);
			return user;
		}).map(UserDTO::new);
	}

	public void deleteUser(String login) {
		userRepository.findOneByLogin(login).ifPresent(user -> {
			socialService.deleteUserSocialConnection(user.getLogin());
			userRepository.delete(user);
			cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
			cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
			log.debug("Deleted User: {}", user);
		});
	}

	public void changePassword(String password) {
		SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(user -> {
			String encryptedPassword = passwordEncoder.encode(password);
			user.setPassword(encryptedPassword);
			user.setFirstLogin(false);
			cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
			cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
			log.debug("Changed password for User: {}", user);
		});
	}

	@Transactional(readOnly = true)
	public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
		return userRepository.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER).map(UserDTO::new);
	}

	@Transactional(readOnly = true)
	public List<UserDTO> searchUsers(FilterUserDTO filter) {
		List<User> list = userRepository.searchUsers(filter);
		return list.stream().map(UserDTO::new).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Optional<User> getUserWithAuthoritiesByLogin(String login) {
		return userRepository.findOneWithAuthoritiesByLogin(login);
	}

	@Transactional(readOnly = true)
	public Optional<User> getUserWithAuthorities(Long id) {
		return userRepository.findOneWithAuthoritiesById(id);
	}

	@Transactional(readOnly = true)
	public Optional<User> getUserWithAuthorities() {
		return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
	}

	/**
	 * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
	 * 30 days.
	 * <p>
	 * This is scheduled to get fired everyday, at midnight.
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void removeOldPersistentTokens() {
		LocalDate now = LocalDate.now();
		persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1)).forEach(token -> {
			log.debug("Deleting token {}", token.getSeries());
			User user = token.getUser();
			user.getPersistentTokens().remove(token);
			persistentTokenRepository.delete(token);
		});
	}

	// /**
	// * Not activated users should be automatically deleted after 3 days.
	// * <p>
	// * This is scheduled to get fired everyday, at 01:00 (am).
	// */
	// @Scheduled(cron = "0 0 1 * * ?")
	// public void removeNotActivatedUsers() {
	// List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS));
	// for (User user : users) {
	// log.debug("Deleting not activated user {}", user.getLogin());
	// userRepository.delete(user);
	// cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
	// cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
	// }
	// }

	/**
	 * @return a list of all the authorities
	 */
	public List<String> getAuthorities() {
		return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
	}

	public void bloquear(User user) {
		user.setBloqueado(true);
		userRepository.save(user);
		cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
		cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
	}

	public void desbloquear(User user) {
		user.setBloqueado(false);
		userRepository.save(user);
		cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
		cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
	}

}
