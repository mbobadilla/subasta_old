package ar.com.pmp.subastados.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "event")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Event implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(max = 255)
	@Column(length = 255)
	private String name;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event", fetch = FetchType.EAGER)
	private Set<Lote> lotes = new TreeSet<>();

	@Column
	private boolean active = true;

	@Column(name = "init_date")
	private Instant initDate = null;

	@Column(name = "end_date")
	private Instant endDate = null;

	@Column(name = "notificate_last_minutes")
	private Integer notificateLastMinutes;

	@Column
	private boolean deleted = false;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", length = 255)
	private EventType type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Lote> getLotes() {
		return lotes;
	}

	public void setLotes(Set<Lote> lotes) {
		this.lotes = lotes;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}

	public Integer getNotificateLastMinutes() {
		return notificateLastMinutes;
	}

	public void setNotificateLastMinutes(Integer notificateLastMinutes) {
		this.notificateLastMinutes = notificateLastMinutes;
	}

	public Instant getInitDate() {
		return initDate;
	}

	public void setInitDate(Instant initDate) {
		this.initDate = initDate;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Event() {
		// TODO Auto-generated constructor stub
	}

	public Event(Long id, String name, boolean active, Instant initDate, Instant endDate) {
		super();
		this.id = id;
		this.name = name;
		this.active = active;
		this.initDate = initDate;
		this.endDate = endDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Event) {
			final Event other = (Event) obj;
			return new EqualsBuilder().append(id, other.id).append(name, other.name).isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(13, 17).append(id).append(name).toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Event [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", lotes=");
		builder.append(lotes);
		builder.append("]");
		return builder.toString();
	}

	public boolean isFinished() {
		return !active;
	}

}
