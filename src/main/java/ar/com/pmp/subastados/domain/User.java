package ar.com.pmp.subastados.domain;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ar.com.pmp.subastados.config.Constants;

/**
 * A user.
 */
@Entity
@Table(name = "jhi_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractAuditingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Pattern(regexp = Constants.LOGIN_REGEX)
	@Size(min = 1, max = 100)
	@Column(length = 100, unique = true, nullable = false)
	private String login;

	@JsonIgnore
	@NotNull
	@Size(min = 60, max = 60)
	@Column(name = "password_hash", length = 60)
	private String password;

	@Size(max = 50)
	@Column(name = "first_name", length = 50)
	private String firstName;

	@Size(max = 50)
	@Column(name = "last_name", length = 50)
	private String lastName;

	@Email
	@Size(min = 5, max = 100)
	@Column(length = 100, unique = true)
	private String email;

	@NotNull
	@Column(nullable = false)
	private boolean activated = false;

	@Size(min = 2, max = 6)
	@Column(name = "lang_key", length = 6)
	private String langKey;

	@Size(max = 256)
	@Column(name = "image_url", length = 256)
	private String imageUrl;

	@Size(max = 20)
	@Column(name = "activation_key", length = 20)
	@JsonIgnore
	private String activationKey;

	@Size(max = 20)
	@Column(name = "reset_key", length = 20)
	@JsonIgnore
	private String resetKey;

	@Column(name = "reset_date")
	private Instant resetDate = null;

	// Nuevos campos

	@NotNull
	@Column(name = "accepted_conditions", nullable = false)
	private boolean acceptedConditions = false;

	@Size(max = 20)
	@Column(length = 20)
	private String dni;

	@Column(name = "birthday_date")
	private LocalDate birthdayDate;

	@Size(max = 20)
	@Column(name = "cell_phone", length = 20)
	private String cellPhone;

	@Size(max = 255)
	@Column(length = 255)
	private String address;

	@NotNull
	@Column(name = "email_notifications", nullable = false)
	private boolean emailNotifications = true;

	@Size(max = 5)
	@Column(name = "phone_activation_key", length = 5)
	@JsonIgnore
	private String phoneActivationKey;

	@NotNull
	@Column(name = "phone_notifications", nullable = false)
	private boolean phoneNotifications = true;

	@NotNull
	@Column(name = "phone_valid", nullable = false)
	private boolean phoneValid = false;

	@NotNull
	@Column(name = "email_valid", nullable = false)
	private boolean emailValid = false;

	@Size(max = 255)
	@Column(name = "presented_by", length = 255)
	private String presentedBy;

	@Size(max = 255)
	@Column(name = "postal_code", length = 255)
	private String postalCode;

	@Size(max = 255)
	@Column(name = "city", length = 255)
	private String city;

	@Size(max = 255)
	@Column(name = "user_country", length = 255)
	private String userCountry;

	@Column(name = "first_login")
	private boolean firstLogin = false;

	@Size(max = 1024)
	@Column(name = "comments", length = 1024)
	private String comments;

	@Column(name = "bloqueado")
	private boolean bloqueado = false;
	
	//

	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "jhi_user_authority", joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "authority_name", referencedColumnName = "name") })
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@BatchSize(size = 20)
	private Set<Authority> authorities = new HashSet<>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<PersistentToken> persistentTokens = new HashSet<>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private Set<UserNotification> notifications = new HashSet<>();

	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "user_lote_follower", joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "lote_id", referencedColumnName = "id") })
	private Set<Lote> followLotes = new HashSet<>();

	// FIXME
	// Follow lotes deberia ser un temporal evento a evento, para notificaciones del evento -------- habria que limpiar la lista una vez
	// finalizado el evento
	// Una nueva lista de lotes de interes deberia ser creada

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user", fetch = FetchType.EAGER)
	private Set<DeviceRegistration> deviceRegistrations = new HashSet<>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user", fetch = FetchType.EAGER)
	private Set<EventParticipant> participants = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	// Lowercase the login before saving it in database
	public void setLogin(String login) {
		this.login = StringUtils.lowerCase(login, Locale.ENGLISH);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public String getResetKey() {
		return resetKey;
	}

	public void setResetKey(String resetKey) {
		this.resetKey = resetKey;
	}

	public Instant getResetDate() {
		return resetDate;
	}

	public void setResetDate(Instant resetDate) {
		this.resetDate = resetDate;
	}

	public String getLangKey() {
		return langKey;
	}

	public void setLangKey(String langKey) {
		this.langKey = langKey;
	}

	public Set<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}

	public Set<PersistentToken> getPersistentTokens() {
		return persistentTokens;
	}

	public void setPersistentTokens(Set<PersistentToken> persistentTokens) {
		this.persistentTokens = persistentTokens;
	}

	public boolean isAcceptedConditions() {
		return acceptedConditions;
	}

	public void setAcceptedConditions(boolean acceptedConditions) {
		this.acceptedConditions = acceptedConditions;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public LocalDate getBirthdayDate() {
		return birthdayDate;
	}

	public void setBirthdayDate(LocalDate birthdayDate) {
		this.birthdayDate = birthdayDate;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isEmailNotifications() {
		return emailNotifications;
	}

	public void setEmailNotifications(boolean emailNotifications) {
		this.emailNotifications = emailNotifications;
	}

	public String getPhoneActivationKey() {
		return phoneActivationKey;
	}

	public void setPhoneActivationKey(String phoneActivationKey) {
		this.phoneActivationKey = phoneActivationKey;
	}

	public boolean isPhoneNotifications() {
		return phoneNotifications;
	}

	public void setPhoneNotifications(boolean phoneNotifications) {
		this.phoneNotifications = phoneNotifications;
	}

	public boolean isPhoneValid() {
		return phoneValid;
	}

	public void setPhoneValid(boolean phoneValid) {
		this.phoneValid = phoneValid;
	}

	public void setEmailValid(boolean emailValid) {
		this.emailValid = emailValid;
	}

	public boolean isEmailValid() {
		return emailValid;
	}

	public Set<UserNotification> getNotifications() {
		return notifications;
	}

	public void setNotifications(Set<UserNotification> notifications) {
		this.notifications = notifications;
	}

	public Set<Lote> getFollowLotes() {
		return followLotes;
	}

	public void setFollowLotes(Set<Lote> followLotes) {
		this.followLotes = followLotes;
	}

	public String getPresentedBy() {
		return presentedBy;
	}

	public void setPresentedBy(String presentedBy) {
		this.presentedBy = presentedBy;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getUserCountry() {
		return userCountry;
	}

	public void setUserCountry(String userCountry) {
		this.userCountry = userCountry;
	}

	public Set<DeviceRegistration> getDeviceRegistrations() {
		return deviceRegistrations;
	}

	public void setDeviceRegistrations(Set<DeviceRegistration> deviceRegistrations) {
		this.deviceRegistrations = deviceRegistrations;
	}

	public boolean isFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Set<EventParticipant> getParticipants() {
		return participants;
	}

	public void setParticipants(Set<EventParticipant> participants) {
		this.participants = participants;
	}

	public boolean isParticipant(Long eventId) {
		return this.participants.stream().filter(x -> eventId.equals(x.getEventId())).findAny().isPresent();
	}
	
	public boolean isBloqueado() {
		return bloqueado;
	}
	
	public void setBloqueado(boolean bloqueado) {
		this.bloqueado = bloqueado;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		User user = (User) o;
		return !(user.getId() == null || getId() == null) && Objects.equals(getId(), user.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [id=");
		builder.append(id);
		builder.append(", login=");
		builder.append(login);
		builder.append(", password=");
		builder.append(password);
		builder.append(", firstName=");
		builder.append(firstName);
		builder.append(", lastName=");
		builder.append(lastName);
		builder.append(", email=");
		builder.append(email);
		builder.append(", activated=");
		builder.append(activated);
		builder.append("]");
		return builder.toString();
	}

}
