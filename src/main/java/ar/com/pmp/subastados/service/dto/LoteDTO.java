package ar.com.pmp.subastados.service.dto;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import ar.com.pmp.subastados.domain.Lote;
import ar.com.pmp.subastados.domain.User;

public class LoteDTO implements Comparable<LoteDTO> {

	private Long id;
	private Long eventId;

	private ProductDTO product;

	private Integer orden;
	private Instant endDate;
	private Integer minutesToIncrement;
	private Integer incrementPeriod;
	private Double initialPrice;
	private boolean finished;

	private BidDTO lastBid;

	private Set<String> followers;

	public LoteDTO() {
		// Empty constructor needed for Jackson.
	}

	public LoteDTO(Lote source) {
		this.id = source.getId();
		this.eventId = source.getEvent().getId();

		this.product = new ProductDTO(source.getProduct());

		this.orden = source.getOrden();
		this.endDate = source.getEndDate();
		this.minutesToIncrement = source.getMinutesToIncrement();
		this.incrementPeriod = source.getIncrementPeriod();
		this.initialPrice = source.getInitialPrice();
		this.finished = source.isFinished();

		if (source.getLastBid() != null)
			this.lastBid = new BidDTO(source.getLastBid());

		this.followers = source.getFollowers().stream().map(User::getLogin).collect(Collectors.toSet());
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

	public ProductDTO getProduct() {
		return product;
	}

	public void setProduct(ProductDTO product) {
		this.product = product;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}

	public Integer getMinutesToIncrement() {
		return minutesToIncrement;
	}

	public void setMinutesToIncrement(Integer minutesToIncrement) {
		this.minutesToIncrement = minutesToIncrement;
	}

	public Integer getIncrementPeriod() {
		return incrementPeriod;
	}

	public void setIncrementPeriod(Integer incrementPeriod) {
		this.incrementPeriod = incrementPeriod;
	}

	public Double getInitialPrice() {
		return initialPrice;
	}

	public void setInitialPrice(Double initialPrice) {
		this.initialPrice = initialPrice;
	}

	public BidDTO getLastBid() {
		return lastBid;
	}

	public void setLastBid(BidDTO lastBid) {
		this.lastBid = lastBid;
	}

	@Override
	public int compareTo(LoteDTO o) {
		return this.orden.compareTo(o.orden);
	}

	public Set<String> getFollowers() {
		return followers;
	}

	public void setFollowers(Set<String> followers) {
		this.followers = followers;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LoteDTO [id=");
		builder.append(id);
		builder.append(", eventId=");
		builder.append(eventId);
		builder.append(", product=");
		builder.append(product);
		builder.append(", orden=");
		builder.append(orden);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", minutesToIncrement=");
		builder.append(minutesToIncrement);
		builder.append(", incrementPeriod=");
		builder.append(incrementPeriod);
		builder.append(", initialPrice=");
		builder.append(initialPrice);
		builder.append(", lastBid=");
		builder.append(lastBid);
		builder.append("]");
		return builder.toString();
	}

}
