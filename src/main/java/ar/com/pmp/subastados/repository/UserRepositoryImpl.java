package ar.com.pmp.subastados.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

import ar.com.pmp.subastados.config.Constants;
import ar.com.pmp.subastados.domain.EventParticipant;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.service.dto.FilterUserDTO;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

	private static final String LOGIN = "login";

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public List<User> searchUsers(FilterUserDTO filter) {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);

		Root<User> userRoot = criteria.from(User.class);
		criteria.select(userRoot);

		ArrayList<Predicate> predicates = Lists.newArrayList();

		// Saca usuario anonimo de la lista
		predicates.add(builder.notEqual(userRoot.get(LOGIN), Constants.ANONYMOUS_USER));

		// Usuario
		if (StringUtils.isNotBlank(filter.getLogin()))
			predicates.add(builder.like(userRoot.get(LOGIN), "%" + filter.getLogin() + "%"));

		// Nombre
		if (StringUtils.isNotBlank(filter.getFirstName()))
			predicates.add(builder.like(userRoot.get("firstName"), "%" + filter.getFirstName() + "%"));

		// Apellido
		if (StringUtils.isNotBlank(filter.getLastName()))
			predicates.add(builder.like(userRoot.get("lastName"), "%" + filter.getLastName() + "%"));

		// Ciudad
		if (StringUtils.isNotBlank(filter.getCity()))
			predicates.add(builder.like(userRoot.get("city"), "%" + filter.getCity() + "%"));

		// Pais
		if (StringUtils.isNotBlank(filter.getCountry()))
			predicates.add(builder.equal(userRoot.get("userCountry"), filter.getCountry()));

		// Mail Valido
		if (filter.isValidatedEmail() && !filter.isNotValidatedEmail()) {
			predicates.add(builder.equal(userRoot.get("emailValid"), true));
		} else if (!filter.isValidatedEmail() && filter.isNotValidatedEmail()) {
			predicates.add(builder.equal(userRoot.get("emailValid"), false));
		}

		// Phone Valido
		if (filter.isValidatedPhone() && !filter.isNotValidatedPhone()) {
			predicates.add(builder.equal(userRoot.get("phoneValid"), true));
		} else if (!filter.isValidatedPhone() && filter.isNotValidatedPhone()) {
			predicates.add(builder.equal(userRoot.get("phoneValid"), false));
		}
		
		// Usuario bloqueado
		if (filter.isBlocked()) {
			predicates.add(builder.equal(userRoot.get("bloqueado"), true));
		} else {
			predicates.add(builder.equal(userRoot.get("bloqueado"), false));
		}
		
		// Usuario activado
		if (filter.isActivated()) {
			predicates.add(builder.equal(userRoot.get("activated"), true));
		} else {
			predicates.add(builder.equal(userRoot.get("activated"), false));
		}

		// Evento
		if (filter.getEventId() != null && filter.getEventId() != -1L) {
			if (filter.isParticipant()) {
				Join<User, EventParticipant> participants = userRoot.join("participants");
				predicates.add(builder.equal(participants.get("eventId"), filter.getEventId()));
			} else {

				// SELECT *
				// FROM jhi_user u
				// where u.id not in (SELECT ep.user_id FROM event_participant ep where ep.event_id = 8);

				Subquery<Long> subquery = criteria.subquery(Long.class);
				Root<EventParticipant> fromEventParticipant = subquery.from(EventParticipant.class);
				subquery.select(fromEventParticipant.get("user")).where(builder.equal(fromEventParticipant.get("eventId"), filter.getEventId()));

				predicates.add(builder.not(userRoot.get("id").in(subquery)));
			}
		}

		criteria.where(predicates.toArray(new Predicate[0]));

		if (StringUtils.isNotBlank(filter.getSortBy())) {
			Path<Object> sortBy = userRoot.get(filter.getSortBy());
			Order order = filter.isSortAsc() ? builder.asc(sortBy) : builder.desc(sortBy);
			criteria.orderBy(order);
		} else {
			criteria.orderBy(builder.asc(userRoot.get("id")));
		}

		return entityManager.createQuery(criteria).setMaxResults(100).getResultList();
	}

}
