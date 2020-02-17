package ar.com.pmp.subastados.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import ar.com.pmp.subastados.domain.Event;

@Repository
public class EventRepositoryImpl implements EventRepositoryCustom {

	private final Logger log = LoggerFactory.getLogger(EventRepositoryImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> findAllWithoutLotes() {

		Query q = entityManager.createNativeQuery("SELECT e.id, e.name, e.active, e.init_date, e.end_date FROM event e WHERE e.active = false AND e.deleted = false ORDER BY e.init_date DESC");

		List<Object[]> events = q.getResultList();

		return events.stream().map(record -> this.toEvent(record)).collect(Collectors.toList());
	}

	private Event toEvent(Object[] record) {
		Long id = ((BigInteger) record[0]).longValue();
		String name = (String) record[1];
		Boolean active = (Boolean) record[2];

		Date from = (Date) record[3];
		Date to = (Date) record[4];

		return new Event(id, name, active, from.toInstant(), to.toInstant());
	}

	public void deleteAllFollowers() {
		Query query = entityManager.createNativeQuery("DELETE FROM user_lote_follower");
		int executeUpdate = query.executeUpdate();

		log.info("Se borraron {} followers.", executeUpdate);
	}
}
