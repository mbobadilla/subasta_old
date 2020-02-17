package ar.com.pmp.subastados.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * An ActivityEvent.
 */
@Entity
@Table(name = "activity_event")
public class ActivityEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", length = 50)
	private ActivityEventType type;

	@Column(name = "login")
	private String login;

	@Column(name = "date")
	private Instant date = Instant.now();

	@Column(name = "detail")
	private String detail;

	@Column(name = "extra", length = 255)
	private String extra;

	@Column(name = "event_name", length = 255)
	private String eventName;

	@Column(name = "price")
	private Double price;

	@Column(name = "product_name", length = 255)
	private String productName;

	@Column(name = "message", length = 255)
	private String messageName;

	public ActivityEvent() {
		// TODO Auto-generated constructor stub
	}

	public ActivityEvent(ActivityEventType type, String login, String eventName, Double price, String productName, String messageName, String extra) {
		super();
		this.type = type;
		this.login = login;
		this.eventName = eventName;
		this.price = price;
		this.productName = productName;
		this.messageName = messageName;
		this.extra = extra;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ActivityEventType getType() {
		return type;
	}

	public void setType(ActivityEventType type) {
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
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ActivityEvent subscriber = (ActivityEvent) o;
		return !(subscriber.getId() == null || getId() == null) && Objects.equals(getId(), subscriber.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}
}
