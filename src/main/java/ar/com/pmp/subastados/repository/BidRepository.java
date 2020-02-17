package ar.com.pmp.subastados.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.pmp.subastados.domain.Bid;

/**
 * Spring Data JPA repository for the Bid entity.
 */
@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

	Page<Bid> findByLoteIdOrderByDateDesc(Long loteId, Pageable pageable);

	Optional<Bid> findTop1ByLoteIdOrderByDateDesc(Long loteId);

	List<Bid> findByLoteIdOrderByDateDesc(Long loteId);
}
