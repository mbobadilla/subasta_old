package ar.com.pmp.subastados.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.com.pmp.subastados.domain.GeneralConfiguration;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface GeneralConfigurationRepository extends JpaRepository<GeneralConfiguration, String> {
}
