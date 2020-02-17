package ar.com.pmp.subastados.events;

import org.springframework.context.ApplicationEvent;

import ar.com.pmp.subastados.domain.Event;

public class LastMinutesNotificationEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public LastMinutesNotificationEvent(Event event) {
		super(event);
	}

}