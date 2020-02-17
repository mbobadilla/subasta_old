package ar.com.pmp.subastados.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.pmp.subastados.domain.Subscriber;
import ar.com.pmp.subastados.repository.SubscriberRepository;
import ar.com.pmp.subastados.service.dto.SubscriberDTO;

/**
 * Service class for managing Subscriber.
 */
@Service
@Transactional
public class SubscriberService {

	private final Logger log = LoggerFactory.getLogger(SubscriberService.class);

	@Autowired
	private SubscriberRepository subscriberRepository;

	public Subscriber createSubscriber(SubscriberDTO subscriberDTO) {
		Subscriber subscriber = new Subscriber();
		subscriber.setName(subscriberDTO.getName());
		subscriber.setEmail(subscriberDTO.getEmail());
		subscriber.setCellPhone(subscriberDTO.getCellPhone());
		subscriber.setMessage(subscriberDTO.getMessage());
		subscriberRepository.save(subscriber);

		log.debug("Created a Subscriber: {}", subscriber);
		return subscriber;
	}

	@Transactional(readOnly = true)
	public Page<SubscriberDTO> getAllSubscribers(Pageable pageable) {
		return subscriberRepository.findAllByOrderByCreatedDateDesc(pageable).map(SubscriberDTO::new);
	}

	public void deleteSubscriber(Long id) {
		subscriberRepository.findOneById(id).ifPresent(subscriber -> {
			subscriberRepository.delete(subscriber);
			log.debug("Deleted Subscriber: {}", subscriber);
		});
	}

}
