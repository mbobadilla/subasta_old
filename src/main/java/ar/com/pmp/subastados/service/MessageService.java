package ar.com.pmp.subastados.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.pmp.subastados.domain.Message;
import ar.com.pmp.subastados.domain.MessageState;
import ar.com.pmp.subastados.domain.MessageTemplate;
import ar.com.pmp.subastados.domain.MessageType;
import ar.com.pmp.subastados.domain.MessageUser;
import ar.com.pmp.subastados.domain.MessageUserState;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.repository.MessageRepository;
import ar.com.pmp.subastados.repository.MessageTemplateRepository;
import ar.com.pmp.subastados.repository.MessageUserRepository;
import ar.com.pmp.subastados.repository.UserRepository;
import ar.com.pmp.subastados.service.dto.MessageDTO;
import ar.com.pmp.subastados.service.dto.MessageUserDTO;
import ar.com.pmp.subastados.web.rest.errors.EntityNotFoundException;

/**
 * Service class for managing Messages.
 */
@Service
@Transactional
public class MessageService {

	private final Logger log = LoggerFactory.getLogger(MessageService.class);

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private MessageUserRepository messageUserRepository;

	@Autowired
	private MessageTemplateRepository messageTemplateRepository;

	@Autowired
	private UserRepository userRepository;
	
	public Message createMessage(MessageDTO messageDTO) {
		Message message = new Message();

		message.setType(MessageType.valueOf(messageDTO.getType()));
		message.setState(MessageState.SENDING);

		MessageTemplate messageTemplate = messageTemplateRepository.findOneById(messageDTO.getMessageTemplateDTO().getId()).orElseThrow(() -> new EntityNotFoundException("Template no existe"));

		message.setMessageTemplate(messageTemplate);
		message.setSubject(messageTemplate.getSubject());
		message.setBody(messageTemplate.getBody());
		message.setHtml(messageTemplate.isHtml());

		message.setDestinataries(messageDTO.getDestinataries().stream().map(messageUserDTO -> createMessageUser(messageUserDTO, message)).collect(Collectors.toSet()));

		messageRepository.save(message);

		log.debug("Created a Message: {}", message);
		return message;
	}

	private MessageUser createMessageUser(MessageUserDTO dto, Message message) {
		User user = userRepository.findOne(dto.getContactId());
		return new MessageUser(user, message, MessageUserState.WAITING, 0);
	}

	public void updateMessageUser(MessageUser messageUser) {
		messageUserRepository.save(messageUser);

		Message message = messageRepository.findOne(messageUser.getMessage().getId());
		if (message.getDestinataries().stream().allMatch(x -> !MessageUserState.WAITING.equals(x.getState()))) {
			if (message.getDestinataries().stream().anyMatch(x -> !MessageUserState.SENDED.equals(x.getState()))) {
				message.setState(MessageState.FINISHED_ERROR);
			} else {
				message.setState(MessageState.FINISHED);
			}
			message.setEndDate(Instant.now());
		}
	}

	@Transactional(readOnly = true)
	public Page<MessageDTO> getAllMessages(Pageable pageable) {
		return messageRepository.findAllByOrderByCreatedDateDesc(pageable).map(MessageDTO::new);
	}
	
	@Transactional(readOnly = true)
	public List<Message> findByState(MessageState state) {
		return messageRepository.findByState(state);
	}

	@Transactional(readOnly = true)
	public MessageDTO findById(Long id) {
		return new MessageDTO(messageRepository.findOne(id));
	}

	public Message findToRetry(Long id) {
		Message message = messageRepository.findOne(id);
		
		message.getDestinataries().forEach(destinatary -> {
			if(!MessageUserState.SENDED.equals(destinatary.getState())) { // Si no esta enviado se reintenta
				destinatary.setState(MessageUserState.WAITING);
			}
		});

		long count = message.getDestinataries().stream().filter(mu -> MessageUserState.WAITING.equals(mu.getState())).count();
		if(count > 0) {
			message.setState(MessageState.SENDING);
			messageRepository.save(message);
		}
		
		return message;
	}

}
