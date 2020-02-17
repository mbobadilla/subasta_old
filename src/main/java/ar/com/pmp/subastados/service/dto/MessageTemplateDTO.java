package ar.com.pmp.subastados.service.dto;

import java.time.Instant;

import javax.validation.constraints.Size;

import ar.com.pmp.subastados.domain.MessageTemplate;
import ar.com.pmp.subastados.domain.MessageType;

/**
 * A DTO representing a MessageTemplate
 */
public class MessageTemplateDTO {

	private Long id;

	private MessageType type;

	@Size(max = 255)
	private String name;

	@Size(max = 255)
	private String subject;

	private String body;

	private Instant date;

	private boolean html;

	public MessageTemplateDTO() {
		// Empty constructor needed for Jackson.
	}

	public MessageTemplateDTO(MessageTemplate messageTemplate) {
		this.id = messageTemplate.getId();
		this.type = messageTemplate.getType();
		this.name = messageTemplate.getName();
		this.subject = messageTemplate.getSubject();
		this.body = messageTemplate.getBody();
		this.date = messageTemplate.getDate();
		this.html = messageTemplate.isHtml();
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageTemplateDTO [id=");
		builder.append(id);
		builder.append(", type=");
		builder.append(type);
		builder.append(", name=");
		builder.append(name);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", body=");
		builder.append(body);
		builder.append(", date=");
		builder.append(date);
		builder.append("]");
		return builder.toString();
	}

}
