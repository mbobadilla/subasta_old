package ar.com.pmp.subastados.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.pmp.subastados.domain.DeviceRegistration;
import ar.com.pmp.subastados.domain.User;

/**
 * Spring Data JPA repository for the DeviceRegistration entity.
 */
@Repository
public interface DeviceRegistrationRepository extends JpaRepository<DeviceRegistration, Long> {

	DeviceRegistration findOneByUserAndRegistrationId(User user, String registrationId);

	DeviceRegistration findOneByRegistrationId(String registrationId);

	// XXX el metod findOne trae mas de un objeto
	List<DeviceRegistration> findByRegistrationId(String registrationId);

}
