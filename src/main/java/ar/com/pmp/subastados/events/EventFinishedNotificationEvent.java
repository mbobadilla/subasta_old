package ar.com.pmp.subastados.events;

import org.springframework.context.ApplicationEvent;

import ar.com.pmp.subastados.domain.Event;

public class EventFinishedNotificationEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public EventFinishedNotificationEvent(Event event) {
		super(event);
	}

}