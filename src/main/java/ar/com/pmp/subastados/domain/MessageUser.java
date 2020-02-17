package ar.com.pmp.subastados.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A message user.
 */
@Entity
@Table(name = "message_user")
public class MessageUser implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final Integer MAX_ATTEMPTS = 2;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "message_id")
	private Message message;

	@Enumerated(EnumType.STRING)
	@Column(name = "state", length = 50)
	private MessageUserState state = MessageUserState.WAITING;

	@Column(name = "attempts")
	private Integer attempts = 0;

	public MessageUser() {
	}

	public MessageUser(User user, Message message, MessageUserState state, Integer attempts) {
		super();
		this.user = user;
		this.message = message;
		this.state = state;
		this.attempts = attempts;
	}

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

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public MessageUserState getState() {
		return state;
	}

	public void setState(MessageUserState state) {
		this.state = state;
	}

	public Integer getAttempts() {
		return attempts;
	}

	public void setAttempts(Integer attempts) {
		this.attempts = attempts;
	}

	public void incrementAttempts() {
		this.attempts += 1;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		MessageUser subscriber = (MessageUser) o;
		return !(subscriber.getId() == null || getId() == null) && Objects.equals(getId(), subscriber.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}
}
