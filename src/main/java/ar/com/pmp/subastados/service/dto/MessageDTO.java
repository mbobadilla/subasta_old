package ar.com.pmp.subastados.service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import ar.com.pmp.subastados.domain.Message;
import ar.com.pmp.subastados.domain.MessageState;
import ar.com.pmp.subastados.domain.MessageType;
import ar.com.pmp.subastados.domain.MessageUserState;

/**
 * A DTO representing a Message
 */
public class MessageDTO {

	private Long id;
	private String type;
	private String state;

	private MessageTemplateDTO messageTemplateDTO;
	private String subject;
	private String body;

	private Set<MessageUserDTO> destinataries = new HashSet<>();

	private boolean html;

	private String createdBy;
	private Instant createdDate;
	private Instant endDate;

	private Integer completeness;

	private String sended;
	private String failure;

	public MessageDTO() {
		// Empty constructor needed for Jackson.
	}

	public MessageDTO(Message message) {
		this.id = message.getId();
		this.type = message.getType().getValue();
		this.state = message.getState().getValue();
		this.messageTemplateDTO = new MessageTemplateDTO(message.getMessageTemplate());
		this.subject = message.getSubject();
		this.body = message.getBody();
		this.destinataries = message.getDestinataries().stream().map(MessageUserDTO::new).collect(Collectors.toSet());
		this.html = message.isHtml();
		this.createdBy = message.getCreatedBy();
		this.createdDate = message.getCreatedDate();
		this.endDate = message.getEndDate();

		long enviados = message.getDestinataries().stream().filter(x -> MessageUserState.SENDED.equals(x.getState())).count();

		Double percentage = Double.valueOf(enviados) / message.getDestinataries().size() * 100D;

		this.completeness = percentage.intValue();
		this.sended = String.format("%s / %s", enviados, message.getDestinataries().size());

		long fallidos = message.getDestinataries().stream().filter(x -> MessageUserState.CONTACT_ERROR.equals(x.getState()) || MessageUserState.SENDING_ERROR.equals(x.getState())).count();
		this.failure = String.format("%s / %s", fallidos, message.getDestinataries().size());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public MessageTemplateDTO getMessageTemplateDTO() {
		return messageTemplateDTO;
	}

	public void setMessageTemplateDTO(MessageTemplateDTO messageTemplateDTO) {
		this.messageTemplateDTO = messageTemplateDTO;
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

	public Set<MessageUserDTO> getDestinataries() {
		return destinataries;
	}

	public void setDestinataries(Set<MessageUserDTO> destinataries) {
		this.destinataries = destinataries;
	}

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
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

	public Integer getCompleteness() {
		return completeness;
	}

	public void setCompleteness(Integer completeness) {
		this.completeness = completeness;
	}

	public String getSended() {
		return sended;
	}

	public void setSended(String sended) {
		this.sended = sended;
	}

	public String getFailure() {
		return failure;
	}

	public void setFailure(String failure) {
		this.failure = failure;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageDTO [id=");
		builder.append(id);
		builder.append(", type=");
		builder.append(type);
		builder.append(", state=");
		builder.append(state);
		builder.append(", messageTemplateDTO=");
		builder.append(messageTemplateDTO);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", body=");
		builder.append(body);
		builder.append(", destinataries=");
		builder.append(destinataries);
		builder.append(", createdBy=");
		builder.append(createdBy);
		builder.append(", createdDate=");
		builder.append(createdDate);
		builder.append("]");
		return builder.toString();
	}

}
