package ar.com.pmp.subastados.repository;

import java.util.List;

import ar.com.pmp.subastados.domain.Event;

public interface EventRepositoryCustom {

	List<Event> findAllWithoutLotes();

	void deleteAllFollowers();
}
