package ar.com.pmp.subastados.domain;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
@Entity
@Table(name = "device_registration")
public class DeviceRegistration implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "registration_id", length = 255)
	private String registrationId;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "platform", length = 255)
	private String platform;

	@Column(name = "version", length = 255)
	private String version;

	@Column(name = "model", length = 255)
	private String model;

	@Column(name = "date")
	private Instant date = Instant.now();

	public DeviceRegistration() {
		super();
	}

	public DeviceRegistration(Long id, String registrationId, User user, String platform, String version, String model, Instant date) {
		super();
		this.id = id;
		this.registrationId = registrationId;
		this.user = user;
		this.platform = platform;
		this.version = version;
		this.model = model;
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeviceRegistration [id=");
		builder.append(id);
		builder.append(", registrationId=");
		builder.append(registrationId);
		builder.append(", user=");
		builder.append(user.getLogin());
		builder.append(", platform=");
		builder.append(platform);
		builder.append(", version=");
		builder.append(version);
		builder.append(", model=");
		builder.append(model);
		builder.append(", date=");
		builder.append(date);
		builder.append("]");
		return builder.toString();
	}

}
