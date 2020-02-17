package ar.com.pmp.subastados.events;

import org.springframework.context.ApplicationEvent;

import ar.com.pmp.subastados.domain.Lote;
import ar.com.pmp.subastados.events.dto.RefreshLoteDTO;
import ar.com.pmp.subastados.service.dto.BidDTO;

public class RefreshEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public RefreshEvent(Lote lote, BidDTO lastBidDTO) {
		super(new RefreshLoteDTO(lote, lastBidDTO));
	}

}