package ar.com.pmp.subastados.events;

import org.springframework.context.ApplicationEvent;

public class FinishLote extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public FinishLote(Long loteId) {
		super(loteId);
	}

}