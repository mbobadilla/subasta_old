package ar.com.pmp.subastados.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.pmp.subastados.domain.Subscriber;

/**
 * Spring Data JPA repository for the Subscriber entity.
 */
@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

	Optional<Subscriber> findOneById(Long id);

	Optional<Subscriber> findOneByEmailIgnoreCase(String email);

	Page<Subscriber> findAllByOrderByCreatedDateDesc(Pageable pageable);

}
