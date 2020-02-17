package ar.com.pmp.subastados.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.pmp.subastados.domain.MessageTemplate;
import ar.com.pmp.subastados.domain.MessageType;
import ar.com.pmp.subastados.repository.MessageTemplateRepository;
import ar.com.pmp.subastados.service.dto.MessageTemplateDTO;

/**
 * Service class for managing Message Templates.
 */
@Service
@Transactional
public class MessageTemplateService {

	private final Logger log = LoggerFactory.getLogger(MessageTemplateService.class);

	@Autowired
	private MessageTemplateRepository messageTemplateRepository;

	public MessageTemplate createTemplate(MessageTemplateDTO messageTemplateDTO) {
		MessageTemplate messageTemplate = new MessageTemplate();

		messageTemplate.setType(messageTemplateDTO.getType());
		messageTemplate.setName(messageTemplateDTO.getName());
		messageTemplate.setSubject(messageTemplateDTO.getSubject());
		messageTemplate.setBody(messageTemplateDTO.getBody());
		messageTemplate.setDate(Instant.now());
		messageTemplate.setHtml(messageTemplateDTO.isHtml());

		messageTemplateRepository.save(messageTemplate);

		log.debug("Created a Message Template: {}", messageTemplate);
		return messageTemplate;
	}

	public void updateTemplate(MessageTemplateDTO messageTemplateDTO) {
		messageTemplateRepository.findOneById(messageTemplateDTO.getId()).ifPresent(messageTemplate -> {
			messageTemplate.setName(messageTemplateDTO.getName());
			messageTemplate.setSubject(messageTemplateDTO.getSubject());
			messageTemplate.setBody(messageTemplateDTO.getBody());
			messageTemplate.setHtml(messageTemplateDTO.isHtml());
			log.debug("Updated Message Template: {}", messageTemplate);
		});
	}

	@Transactional(readOnly = true)
	public List<MessageTemplateDTO> findAllByType(MessageType type) {
		return messageTemplateRepository.findAllByTypeOrderByDateDesc(type).stream().map(MessageTemplateDTO::new).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<MessageTemplateDTO> findTop10ByType(MessageType type) {
		return messageTemplateRepository.findTop10ByTypeOrderByDateDesc(type).stream().map(MessageTemplateDTO::new).collect(Collectors.toList());
	}

	public void deleteTemplate(Long id) {
		messageTemplateRepository.findOneById(id).ifPresent(template -> {
			messageTemplateRepository.delete(template);
			log.debug("Deleted Message Template: {}", template);
		});
	}

}
