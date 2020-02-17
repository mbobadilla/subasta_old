package ar.com.pmp.subastados.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.pmp.subastados.domain.MessageUser;

@Repository
public interface MessageUserRepository extends JpaRepository<MessageUser, Long> {

}
