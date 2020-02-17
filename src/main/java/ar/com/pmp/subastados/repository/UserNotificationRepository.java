package ar.com.pmp.subastados.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.pmp.subastados.domain.NotificationType;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.domain.UserNotification;

/**
 * Spring Data JPA repository for the DeviceRegistration entity.
 */
@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

	List<UserNotification> findByUser(User user);

	Optional<List<UserNotification>> findByUserAndType(User user, NotificationType type);
}
