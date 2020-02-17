package ar.com.pmp.subastados.service.dto;

import java.time.Instant;

import ar.com.pmp.subastados.domain.Bid;

public class BidDTO implements Comparable<BidDTO> {

	private Long id;
	private Long loteId;
	private Long eventId;

	private Double price;
	private Instant date;

	private Long userId;
	private String userLogin;
	private String firstName;
	private String lastName;
	private String presentedBy;
	private String userCountry;

	public BidDTO() {
		// Empty constructor needed for Jackson.
	}

	public BidDTO(Bid source) {
		this.id = source.getId();
		this.loteId = source.getLote().getId();
		this.eventId = source.getLote().getEvent().getId();
		this.userId = source.getUser().getId();
		this.userLogin = source.getUser().getLogin();
		this.firstName = source.getUser().getFirstName();
		this.lastName = source.getUser().getLastName();
		this.price = source.getPrice();
		this.date = source.getDate();
		this.presentedBy = source.getUser().getPresentedBy();
		this.userCountry = source.getUser().getUserCountry();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Long getLoteId() {
		return loteId;
	}

	public void setLoteId(Long loteId) {
		this.loteId = loteId;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	public String getPresentedBy() {
		return presentedBy;
	}

	public void setPresentedBy(String presentedBy) {
		this.presentedBy = presentedBy;
	}

	public String getUserCountry() {
		return userCountry;
	}

	public void setUserCountry(String userCountry) {
		this.userCountry = userCountry;
	}

	@Override
	public int compareTo(BidDTO o) {
		return this.date.compareTo(o.date);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BidDTO [id=");
		builder.append(id);
		builder.append(", loteId=");
		builder.append(loteId);
		builder.append(", price=");
		builder.append(price);
		builder.append(", date=");
		builder.append(date);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", userLogin=");
		builder.append(userLogin);
		builder.append(", firstName=");
		builder.append(firstName);
		builder.append(", lastName=");
		builder.append(lastName);
		builder.append("]");
		return builder.toString();
	}

}
