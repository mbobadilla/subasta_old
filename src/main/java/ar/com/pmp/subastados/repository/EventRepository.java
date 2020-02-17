package ar.com.pmp.subastados.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ar.com.pmp.subastados.domain.Event;

/**
 * Spring Data JPA repository for the Event entity.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

	String EVENT_BY_ID_CACHE = "eventById";

	Page<Event> findAllByOrderByIdDesc(Pageable pageable);

	// @Cacheable(cacheNames = EVENT_BY_ID_CACHE)
	Optional<Event> findById(Long id);

	// Event findFirstByOrderByIdDesc();

	@Query("SELECT max(ev.id) FROM Event ev")
	Long getMaxId();

	@Modifying
	@Query("update Event ev set ev.active = false")
	void setInactiveAll();
}
