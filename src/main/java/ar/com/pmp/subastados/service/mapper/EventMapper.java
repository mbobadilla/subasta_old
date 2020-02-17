package ar.com.pmp.subastados.service.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ar.com.pmp.subastados.domain.Event;
import ar.com.pmp.subastados.service.dto.EventDTO;

@Service
public class EventMapper {

	public EventDTO persistenceToDTO(Event source, boolean lotes) {
		return new EventDTO(source, lotes);
	}

	public List<EventDTO> persistencesToDTOs(List<Event> source, boolean lotes) {
		return source.stream().filter(Objects::nonNull).map(x -> this.persistenceToDTO(x, lotes)).collect(Collectors.toList());
	}

	public List<EventDTO> persistencesToComboDTOs(List<Event> source) {
		return source.stream().filter(Objects::nonNull).map(x -> {
			return new EventDTO(x.getId(), x.getName());
		}).collect(Collectors.toList());
	}
}
