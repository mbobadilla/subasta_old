package ar.com.pmp.subastados.events;

import org.springframework.context.ApplicationEvent;

public class ReloadEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public ReloadEvent() {
		super(System.currentTimeMillis());
	}

}