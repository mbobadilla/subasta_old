package ar.com.pmp.subastados.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "notification")
public class UserNotification implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne
	private User user;

	@Column(name = "type", length = 255)
	private NotificationType type = NotificationType.GENERAL;

	@Size(max = 255)
	@Column(name = "message", length = 255)
	private String message;

	@Column
	private boolean read = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		UserNotification subscriber = (UserNotification) o;
		return !(subscriber.getId() == null || getId() == null) && Objects.equals(getId(), subscriber.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserNotification [id=");
		builder.append(id);
		builder.append(", user=");
		builder.append(user.getLogin());
		builder.append(", type=");
		builder.append(type);
		builder.append(", message=");
		builder.append(message);
		builder.append(", read=");
		builder.append(read);
		builder.append("]");
		return builder.toString();
	}

}
