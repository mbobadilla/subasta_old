package ar.com.pmp.subastados.events;

import java.util.List;

import org.springframework.context.ApplicationEvent;

import ar.com.pmp.subastados.domain.Bid;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.events.dto.BidNotificationDTO;

public class BidNotificationEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public BidNotificationEvent(Bid bid, List<User> followers) {
		super(new BidNotificationDTO(bid, followers));
	}

}