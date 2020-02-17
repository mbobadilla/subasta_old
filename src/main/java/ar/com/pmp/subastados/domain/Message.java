package ar.com.pmp.subastados.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A message.
 */
@Entity
@Table(name = "message")
public class Message extends AbstractAuditingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", length = 255)
	private MessageType type;

	@Enumerated(EnumType.STRING)
	@Column(name = "state", length = 50)
	private MessageState state = MessageState.SENDING;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "template_id")
	private MessageTemplate messageTemplate;

	@Column(name = "subject", length = 255)
	private String subject;

	@Column(name = "body")
	private String body;

	@Column(name = "html")
	private boolean html = false;

	@Column(name = "end_date")
	private Instant endDate;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "message", fetch = FetchType.LAZY)
	private Set<MessageUser> destinataries = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public MessageState getState() {
		return state;
	}

	public void setState(MessageState state) {
		this.state = state;
	}

	public MessageTemplate getMessageTemplate() {
		return messageTemplate;
	}

	public void setMessageTemplate(MessageTemplate messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Set<MessageUser> getDestinataries() {
		return destinataries;
	}

	public void setDestinataries(Set<MessageUser> destinataries) {
		this.destinataries = destinataries;
	}

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Message subscriber = (Message) o;
		return !(subscriber.getId() == null || getId() == null) && Objects.equals(getId(), subscriber.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}
}
