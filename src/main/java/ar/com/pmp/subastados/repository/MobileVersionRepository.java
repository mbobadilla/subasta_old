package ar.com.pmp.subastados.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ar.com.pmp.subastados.domain.MobileVersion;

/**
 * Spring Data JPA repository for the MobileVersion entity.
 */
@Repository
public interface MobileVersionRepository extends CrudRepository<MobileVersion, Long> {

	Optional<MobileVersion> findOneByPlatformAndVersion(String platform, String version);

}
