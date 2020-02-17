package ar.com.pmp.subastados.service.dto;

public class FilterUserDTO {

	// Nombre
	private String firstName;

	// Apellido
	private String lastName;

	// Usuario
	private String login;

	// Ciudad
	private String city;

	// Pais
	private String country;

	// Evento
	private Long eventId;
	private boolean participant;

	private boolean validatedEmail;
	private boolean notValidatedEmail;
	private boolean validatedPhone;
	private boolean notValidatedPhone;

	private boolean blocked;
	private boolean activated;

	// Sort
	private String sortBy = "";
	private boolean sortAsc = true;

	public FilterUserDTO() {
		// Empty constructor needed for Jackson.
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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public boolean isSortAsc() {
		return sortAsc;
	}

	public void setSortAsc(boolean sortAsc) {
		this.sortAsc = sortAsc;
	}

	public boolean isParticipant() {
		return participant;
	}

	public void setParticipant(boolean participant) {
		this.participant = participant;
	}

	public boolean isValidatedEmail() {
		return validatedEmail;
	}

	public void setValidatedEmail(boolean validatedEmail) {
		this.validatedEmail = validatedEmail;
	}

	public boolean isNotValidatedEmail() {
		return notValidatedEmail;
	}

	public void setNotValidatedEmail(boolean notValidatedEmail) {
		this.notValidatedEmail = notValidatedEmail;
	}

	public boolean isValidatedPhone() {
		return validatedPhone;
	}

	public void setValidatedPhone(boolean validatedPhone) {
		this.validatedPhone = validatedPhone;
	}

	public boolean isNotValidatedPhone() {
		return notValidatedPhone;
	}

	public void setNotValidatedPhone(boolean notValidatedPhone) {
		this.notValidatedPhone = notValidatedPhone;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}
}
