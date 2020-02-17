package ar.com.pmp.subastados.service.dto;

import java.time.Instant;

import ar.com.pmp.subastados.domain.ActivityEvent;

/**
 * A DTO representing a Message
 */
public class ActivityEventDTO {

	private Long id;
	private String type;
	private String login;
	private Instant date;
	private String detail;
	private String extra;

	private String eventName;
	private Double price;
	private String productName;
	private String messageName;

	public ActivityEventDTO() {
		// Empty constructor needed for Jackson.
	}

	public ActivityEventDTO(ActivityEvent event) {
		this.id = event.getId();
		this.type = event.getType().getDescription();
		this.login = event.getLogin();
		this.date = event.getDate();
		this.detail = event.getDetail();
		this.extra = event.getExtra();
		this.eventName = event.getEventName();
		this.price = event.getPrice();
		this.productName = event.getProductName();
		this.messageName = event.getMessageName();
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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ActivityEventDTO [id=");
		builder.append(id);
		builder.append(", type=");
		builder.append(type);
		builder.append(", login=");
		builder.append(login);
		builder.append(", date=");
		builder.append(date);
		builder.append(", detail=");
		builder.append(detail);
		builder.append(", extra=");
		builder.append(extra);
		builder.append("]");
		return builder.toString();
	}

}
