package ar.com.pmp.subastados.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.pmp.subastados.config.ScheduleFutureTasks;
import ar.com.pmp.subastados.domain.Bid;
import ar.com.pmp.subastados.domain.Lote;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.events.BidNotificationEvent;
import ar.com.pmp.subastados.events.RefreshEvent;
import ar.com.pmp.subastados.events.WinnerNotificationEvent;
import ar.com.pmp.subastados.repository.BidRepository;
import ar.com.pmp.subastados.repository.LoteRepository;
import ar.com.pmp.subastados.repository.UserRepository;
import ar.com.pmp.subastados.service.dto.BidDTO;
import ar.com.pmp.subastados.web.rest.errors.BadBidException;
import ar.com.pmp.subastados.web.rest.errors.EntityNotFoundException;

@Service
@Transactional
public class TermsFileService {

	private final Logger log = LoggerFactory.getLogger(TermsFileService.class);



	

}
