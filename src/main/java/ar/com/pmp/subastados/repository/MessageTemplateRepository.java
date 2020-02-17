package ar.com.pmp.subastados.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.pmp.subastados.domain.MessageTemplate;
import ar.com.pmp.subastados.domain.MessageType;

/**
 * Spring Data JPA repository for the MessageTemplate entity.
 */
@Repository
public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {

	Optional<MessageTemplate> findOneById(Long id);

	List<MessageTemplate> findAllByTypeOrderByDateDesc(MessageType type);

	List<MessageTemplate> findTop10ByTypeOrderByDateDesc(MessageType type);

}
