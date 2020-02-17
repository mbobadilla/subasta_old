package ar.com.pmp.subastados.events;

import org.springframework.context.ApplicationEvent;

public class WinnerNotificationEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public WinnerNotificationEvent(Long loteId) {
		super(loteId);
	}

}