package ar.com.pmp.subastados.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.pmp.subastados.domain.Lote;

/**
 * Spring Data JPA repository for the Lote entity.
 */
@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

}
