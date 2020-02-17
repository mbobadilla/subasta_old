package ar.com.pmp.subastados.service.dto;

import java.time.Instant;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import ar.com.pmp.subastados.domain.Event;
import ar.com.pmp.subastados.domain.EventType;

public class EventDTO {

	private Long id;
	private String name;

	private Instant initDate;
	private Instant endDate;

	private Set<LoteDTO> lotes = new TreeSet<>();

	private boolean active = true;
	private boolean deleted = false;

	private Integer notificateLastMinutes;

	private EventType type;

	public EventDTO() {
		// Empty constructor needed for Jackson.
	}

	public EventDTO(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public EventDTO(Event source) {
		this(source, false);
	}

	public EventDTO(Event source, boolean lotes) {
		this.id = source.getId();
		this.name = source.getName();
		this.initDate = source.getInitDate();
		this.endDate = source.getEndDate();
		this.active = source.isActive();
		this.notificateLastMinutes = source.getNotificateLastMinutes();
		this.deleted = source.isDeleted();
		this.type = source.getType();

		if (lotes)
			this.lotes.addAll(source.getLotes().stream().map(LoteDTO::new).collect(Collectors.toSet()));
	}

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

	public Set<LoteDTO> getLotes() {
		return lotes;
	}

	public void setLotes(Set<LoteDTO> lotes) {
		this.lotes = lotes;
	}

	public Instant getInitDate() {
		return initDate;
	}

	public void setInitDate(Instant initDate) {
		this.initDate = initDate;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getNotificateLastMinutes() {
		return notificateLastMinutes;
	}

	public void setNotificateLastMinutes(Integer notificateLastMinutes) {
		this.notificateLastMinutes = notificateLastMinutes;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EventDTO [id=").append(id).append(", name=").append(name).append(", lotes=").append(lotes).append("]");
		return builder.toString();
	}

}
