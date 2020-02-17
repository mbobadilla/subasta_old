package ar.com.pmp.subastados.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.pmp.subastados.domain.ActivityEvent;
import ar.com.pmp.subastados.domain.ActivityEventType;

/**
 * Spring Data JPA repository for the Message entity.
 */
@Repository
public interface ActivityEventRepository extends JpaRepository<ActivityEvent, Long> {

	Page<ActivityEvent> findByLoginAndTypeInOrderByDateDesc(String login, ActivityEventType[] types, Pageable pageable);

}
