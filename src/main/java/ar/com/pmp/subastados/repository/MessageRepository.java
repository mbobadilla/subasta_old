package ar.com.pmp.subastados.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.pmp.subastados.domain.Message;
import ar.com.pmp.subastados.domain.MessageState;

/**
 * Spring Data JPA repository for the Message entity.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

	Page<Message> findAllByOrderByCreatedDateDesc(Pageable pageable);
	
	@EntityGraph(attributePaths = "destinataries")
	List<Message> findByState(MessageState state);

}
