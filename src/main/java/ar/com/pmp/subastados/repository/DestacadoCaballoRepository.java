package ar.com.pmp.subastados.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.pmp.subastados.domain.DestacadoCaballo;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface DestacadoCaballoRepository extends JpaRepository<DestacadoCaballo, Long> {

	Page<DestacadoCaballo> findAll(Pageable pageable);

	DestacadoCaballo findByIdcaballo(String idCaballo);
}
