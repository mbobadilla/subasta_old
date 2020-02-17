package ar.com.pmp.subastados.service.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import ar.com.pmp.subastados.config.Constants;
import ar.com.pmp.subastados.domain.Authority;
import ar.com.pmp.subastados.domain.User;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO {

	private Long id;

	@NotBlank
	@Pattern(regexp = Constants.LOGIN_REGEX)
	@Size(min = 1, max = 100)
	private String login;

	@Size(max = 50)
	private String firstName;

	@Size(max = 50)
	private String lastName;

	@Email
	@Size(min = 5, max = 100)
	private String email;

	@Size(max = 256)
	private String imageUrl;

	private boolean activated = false;

	@Size(min = 2, max = 6)
	private String langKey;

	private String createdBy;

	private Instant createdDate;

	private String lastModifiedBy;

	private Instant lastModifiedDate;

	private Set<String> authorities;

	private String dni;
	private String cellPhone;
	private String address;
	private LocalDate birthday;
	private boolean acceptedConditions;
	private boolean emailNotifications;
	private boolean phoneNotifications;
	private boolean phoneValid;
	private boolean emailValid;
	private String presentedBy;
	private String postalCode;
	private String city;
	private String userCountry;
	private boolean firstLogin;
	private String comments;
	private String phoneValidationKey;

	private Set<Long> participants;
	private boolean isLastEventParticipant = false;
	private String lastEvent = null;
	private boolean bloqueado = false;

	public UserDTO() {
		// Empty constructor needed for Jackson.
	}

	public UserDTO(User user) {
		this.id = user.getId();
		this.login = user.getLogin();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.activated = user.getActivated();
		this.imageUrl = user.getImageUrl();
		this.langKey = user.getLangKey();
		this.createdBy = user.getCreatedBy();
		this.createdDate = user.getCreatedDate();
		this.lastModifiedBy = user.getLastModifiedBy();
		this.lastModifiedDate = user.getLastModifiedDate();
		this.authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
		this.dni = user.getDni();
		this.cellPhone = user.getCellPhone();
		this.address = user.getAddress();
		this.birthday = user.getBirthdayDate();
		this.acceptedConditions = user.isAcceptedConditions();
		this.emailNotifications = user.isEmailNotifications();
		this.phoneNotifications = user.isPhoneNotifications();
		this.phoneValid = user.isPhoneValid();
		this.emailValid = user.isEmailValid();
		this.presentedBy = user.getPresentedBy();
		this.postalCode = user.getPostalCode();
		this.city = user.getCity();
		this.userCountry = user.getUserCountry();
		this.firstLogin = user.isFirstLogin();
		this.comments = user.getComments();
		this.participants = user.getParticipants().stream().map(x -> x.getEventId()).collect(Collectors.toSet());
		this.bloqueado = user.isBloqueado();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
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

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getLangKey() {
		return langKey;
	}

	public void setLangKey(String langKey) {
		this.langKey = langKey;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Instant getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Instant lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public Set<String> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<String> authorities) {
		this.authorities = authorities;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
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

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public boolean isAcceptedConditions() {
		return acceptedConditions;
	}

	public void setAcceptedConditions(boolean acceptedConditions) {
		this.acceptedConditions = acceptedConditions;
	}

	public boolean isEmailNotifications() {
		return emailNotifications;
	}

	public void setEmailNotifications(boolean emailNotifications) {
		this.emailNotifications = emailNotifications;
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

	public String getPresentedBy() {
		return presentedBy;
	}

	public void setPresentedBy(String presentedBy) {
		this.presentedBy = presentedBy;
	}

	public boolean isEmailValid() {
		return emailValid;
	}

	public void setEmailValid(boolean emailValid) {
		this.emailValid = emailValid;
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

	public String getPhoneValidationKey() {
		return phoneValidationKey;
	}

	public void setPhoneValidationKey(String phoneValidationKey) {
		this.phoneValidationKey = phoneValidationKey;
	}

	public Set<Long> getParticipants() {
		return participants;
	}

	public void setParticipants(Set<Long> participants) {
		this.participants = participants;
	}

	public void setLastEventParticipant(boolean isLastEventParticipant) {
		this.isLastEventParticipant = isLastEventParticipant;
	}

	public boolean isLastEventParticipant() {
		return isLastEventParticipant;
	}

	public String getLastEvent() {
		return lastEvent;
	}

	public void setLastEvent(String lastEvent) {
		this.lastEvent = lastEvent;
	}
	
	public boolean isBloqueado() {
		return bloqueado;
	}

	public void setBloqueado(boolean bloqueado) {
		this.bloqueado = bloqueado;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserDTO [id=");
		builder.append(id);
		builder.append(", login=");
		builder.append(login);
		builder.append(", firstName=");
		builder.append(firstName);
		builder.append(", lastName=");
		builder.append(lastName);
		builder.append(", email=");
		builder.append(email);
		builder.append(", imageUrl=");
		builder.append(imageUrl);
		builder.append(", activated=");
		builder.append(activated);
		builder.append("]");
		return builder.toString();
	}

}
