package ar.com.pmp.subastados.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import ar.com.pmp.subastados.config.Constants;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.repository.PersistentTokenRepository;
import ar.com.pmp.subastados.repository.UserRepository;
import ar.com.pmp.subastados.security.AuthoritiesConstants;
import ar.com.pmp.subastados.service.EventParticipantService;
import ar.com.pmp.subastados.service.MailService;
import ar.com.pmp.subastados.service.UserService;
import ar.com.pmp.subastados.service.dto.FilterUserDTO;
import ar.com.pmp.subastados.service.dto.UserDTO;
import ar.com.pmp.subastados.web.rest.errors.BadRequestAlertException;
import ar.com.pmp.subastados.web.rest.errors.EmailAlreadyUsedException;
import ar.com.pmp.subastados.web.rest.errors.LoginAlreadyUsedException;
import ar.com.pmp.subastados.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the User entity, and needs to fetch its collection of
 * authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship
 * between User and Authority, and send everything to the client side: there
 * would be no View Model and DTO, a lot less code, and an outer-join which
 * would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities,
 * because people will quite often do relationships with the user, and we don't
 * want them to get the authorities all the time for nothing (for performance
 * reasons). This is the #1 goal: we should not impact our users' application
 * because of this use-case.</li>
 * <li>Not having an outer join causes n+1 requests to the database. This is not
 * a real issue as we have by default a second-level cache. This means on the
 * first HTTP call we do the n+1 requests, but then all authorities come from
 * the cache, so in fact it's much better than doing an outer join (which will
 * get lots of data from the database, for each HTTP call).</li>
 * <li>As this manages users, for security reasons, we'd rather have a DTO
 * layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this
 * case.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

	private final Logger log = LoggerFactory.getLogger(UserResource.class);

	private final UserRepository userRepository;

	private final UserService userService;

	private final MailService mailService;

	private final PersistentTokenRepository persistentTokenRepository;

	@Autowired
	private EventParticipantService eventParticipantService;

	public UserResource(UserRepository userRepository, UserService userService, MailService mailService, PersistentTokenRepository persistentTokenRepository) {

		this.userRepository = userRepository;
		this.userService = userService;
		this.mailService = mailService;
		this.persistentTokenRepository = persistentTokenRepository;
	}

	/**
	 * POST /users : Creates a new user.
	 * <p>
	 * Creates a new user if the login and email are not already used, and sends an
	 * mail with an activation link
	 * @param userDTO
	 * the user to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 * user, or with status 400 (Bad Request) if the login or email is
	 * already in use
	 * @throws URISyntaxException
	 * if the Location URI syntax is incorrect
	 * @throws BadRequestAlertException
	 * 400 (Bad Request) if the login or email is already in use
	 */
	@PostMapping("/users")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) throws URISyntaxException {
		log.debug("REST request to save User : {}", userDTO);

		if (userDTO.getId() != null) {
			throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
			// Lowercase the user login before comparing with database
		} else if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
			throw new LoginAlreadyUsedException();
		} else if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
			throw new EmailAlreadyUsedException();
		} else {
			User newUser = userService.createUser(userDTO);
			mailService.sendCreationEmail(newUser);

			try {
				eventParticipantService.participateToLastEvent(newUser.getLogin());
			} catch (Throwable e) {
				// No hacer nada
			}

			return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin())).headers(HeaderUtil.createAlert("userManagement.created", newUser.getLogin())).body(newUser);
		}
	}

	/**
	 * PUT /users : Updates an existing User.
	 * @param userDTO
	 * the user to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 * user
	 * @throws EmailAlreadyUsedException
	 * 400 (Bad Request) if the email is already in use
	 * @throws LoginAlreadyUsedException
	 * 400 (Bad Request) if the login is already in use
	 */
	@PutMapping("/users")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO) {
		log.debug("REST request to update User : {}", userDTO);
		Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
		if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
			throw new EmailAlreadyUsedException();
		}
		existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
		if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
			throw new LoginAlreadyUsedException();
		}
		Optional<UserDTO> updatedUser = userService.updateUser(userDTO);

		return ResponseUtil.wrapOrNotFound(updatedUser, HeaderUtil.createAlert("userManagement.updated", userDTO.getLogin()));
	}

	/**
	 * POST /users/search : get all users.
	 * @return the ResponseEntity with status 200 (OK) and with body all users
	 */
	@PostMapping("/users/search")
	@Timed
	public ResponseEntity<List<UserDTO>> searchUsers(@RequestBody FilterUserDTO filter) {
		final List<UserDTO> list = userService.searchUsers(filter);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	/**
	 * @return a string list of the all of the roles
	 */
	@GetMapping("/users/authorities")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public List<String> getAuthorities() {
		return userService.getAuthorities();
	}

	/**
	 * GET /users/:login : get the "login" user.
	 * @param login
	 * the login of the user to find
	 * @return the ResponseEntity with status 200 (OK) and with body the "login"
	 * user, or with status 404 (Not Found)
	 */
	@GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
	@Timed
	public ResponseEntity<UserDTO> getUser(@PathVariable String login) {
		log.debug("REST request to get User : {}", login);
		return ResponseUtil.wrapOrNotFound(userService.getUserWithAuthoritiesByLogin(login).map(UserDTO::new));
	}

	/**
	 * DELETE /users/:login : delete the "login" User.
	 * @param login
	 * the login of the user to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@DeleteMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<Void> deleteUser(@PathVariable String login) {
		log.debug("REST request to delete User: {}", login);
		userService.deleteUser(login);
		return ResponseEntity.ok().headers(HeaderUtil.createAlert("userManagement.deleted", login)).build();
	}

	@PatchMapping("/users/{login:" + Constants.LOGIN_REGEX + "}/bloquear")
	@ApiOperation(value = "Bloquea al usuario")
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<Void> bloquearUsuario(@PathVariable String login) {
		log.info("Solicitud de bloquear al usuario {}", login);

		Optional<User> optional = userRepository.findOneByLogin(login);
		if (optional.isPresent()) {
			User user = optional.get();
			userService.bloquear(user);

			persistentTokenRepository.findByUser(user).stream().forEach(t -> persistentTokenRepository.delete(t));

			return ResponseEntity.ok().build();
		}

		return ResponseEntity.notFound().build();
	}

	@PatchMapping("/users/{login:" + Constants.LOGIN_REGEX + "}/desbloquear")
	@ApiOperation(value = "Desbloquea al usuario")
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<Void> desbloquearUsuario(@PathVariable String login) {
		log.info("Solicitud de desbloquear al usuario {}", login);

		Optional<User> user = userRepository.findOneByLogin(login);
		if (user.isPresent()) {
			userService.desbloquear(user.get());

			return ResponseEntity.ok().build();
		}

		return ResponseEntity.notFound().build();
	}

}
