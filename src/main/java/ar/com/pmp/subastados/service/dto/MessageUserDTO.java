package ar.com.pmp.subastados.service.dto;

import ar.com.pmp.subastados.domain.MessageUser;
import ar.com.pmp.subastados.domain.MessageUserState;
import ar.com.pmp.subastados.domain.User;

/**
 * A DTO representing a MessageUserDTO
 */
public class MessageUserDTO {

	private Long id;

	private Long contactId;
	private String contactName;
	private String contact;

	private String state;
	private Integer attempts;

	public MessageUserDTO() {
		// Empty constructor needed for Jackson.
	}

	public MessageUserDTO(MessageUser messageUser) {
		this.id = messageUser.getId();

		User user = messageUser.getUser();

		this.contactId = user.getId();
		this.contactName = user.getFirstName() + " " + user.getLastName();

		switch (messageUser.getMessage().getType()) {
		case MAIL:
			this.contact = user.getEmail();
			break;
		case PHONE:
			this.contact = user.getCellPhone();
			break;
		}

		this.state = messageUser.getState().getValue();
		this.attempts = messageUser.getAttempts();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Integer getAttempts() {
		return attempts;
	}

	public void setAttempts(Integer attempts) {
		this.attempts = attempts;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageUserDTO [id=");
		builder.append(id);
		builder.append(", contactId=");
		builder.append(contactId);
		builder.append(", contactName=");
		builder.append(contactName);
		builder.append(", contact=");
		builder.append(contact);
		builder.append(", state=");
		builder.append(state);
		builder.append(", attempts=");
		builder.append(attempts);
		builder.append("]");
		return builder.toString();
	}

}
