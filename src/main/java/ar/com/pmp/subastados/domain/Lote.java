package ar.com.pmp.subastados.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "lote")
public class Lote implements Serializable, Comparable<Lote> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "id_caballo")
	private String idcaballo;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "product_id")
	private Product product;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "event_id")
	private Event event;

	@Column(name = "orden")
	private Integer orden;

	@Column(name = "end_date")
	private Instant endDate = null;

	@Column(name = "minutes_to_increment")
	private Integer minutesToIncrement;

	@Column(name = "increment_period")
	private Integer incrementPeriod; // (lapso en el que si se realiza una oferta, la fecha de fin se incrementa)

	@Column(name = "initial_price")
	private Double initialPrice;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "last_bid_id")
	private Bid lastBid;

	@Column
	private boolean finished = false;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_lote_follower", joinColumns = { @JoinColumn(name = "lote_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") })
	private Set<User> followers = new HashSet<>();

	// @JsonIgnore
	// @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lote")
	// private Set<Bid> bids = new TreeSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdcaballo() {
		return idcaballo;
	}

	public void setIdcaballo(String idcaballo) {
		this.idcaballo = idcaballo;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
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

	public Bid getLastBid() {
		return lastBid;
	}

	public void setLastBid(Bid lastBid) {
		this.lastBid = lastBid;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public Set<User> getFollowers() {
		return followers;
	}

	public void setFollowers(Set<User> followers) {
		this.followers = followers;
	}

	@Override
	public int compareTo(Lote o) {
		return this.orden.compareTo(o.orden);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Lote) {
			final Lote other = (Lote) obj;
			return new EqualsBuilder().append(id, other.id).append(idcaballo, other.idcaballo).isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(23, 29).append(id).append(idcaballo).toHashCode();
	}
}
