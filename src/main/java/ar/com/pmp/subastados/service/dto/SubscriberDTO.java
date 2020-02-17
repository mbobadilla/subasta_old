package ar.com.pmp.subastados.service.dto;

import java.time.Instant;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import ar.com.pmp.subastados.domain.Subscriber;

/**
 * A DTO representing a Subscriber
 */
public class SubscriberDTO {

	private Long id;

	@NotNull
	@Size(max = 100)
	private String name;

	@NotNull
	@Email
	@Size(min = 5, max = 100)
	private String email;

	@NotNull
	@Size(min = 5, max = 100)
	private String cellPhone;

	private boolean enabled;

	private Instant createdDate;

	@NotNull
	private String message;

	public SubscriberDTO() {
		// Empty constructor needed for Jackson.
	}

	public SubscriberDTO(Subscriber subscriber) {
		this.id = subscriber.getId();
		this.name = subscriber.getName();
		this.email = subscriber.getEmail();
		this.enabled = subscriber.isEnabled();
		this.cellPhone = subscriber.getCellPhone();
		this.createdDate = subscriber.getCreatedDate();
		this.message = subscriber.getMessage();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubscriberDTO [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", email=");
		builder.append(email);
		builder.append(", cellPhone=");
		builder.append(cellPhone);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", createdDate=");
		builder.append(createdDate);
		builder.append(", message=");
		builder.append(message);
		builder.append("]");
		return builder.toString();
	}

}
