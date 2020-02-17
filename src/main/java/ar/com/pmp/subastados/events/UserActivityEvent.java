package ar.com.pmp.subastados.events;

import org.springframework.context.ApplicationEvent;

import ar.com.pmp.subastados.domain.ActivityEvent;

public class UserActivityEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public UserActivityEvent(ActivityEvent activityEvent) {
		super(activityEvent);
	}

}