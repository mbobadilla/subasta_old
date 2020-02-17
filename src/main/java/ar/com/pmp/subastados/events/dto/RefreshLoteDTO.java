package ar.com.pmp.subastados.events.dto;

import java.time.Instant;

import ar.com.pmp.subastados.domain.Lote;
import ar.com.pmp.subastados.service.dto.BidDTO;

public class RefreshLoteDTO {

	private Long loteId;
	private Instant endDate;
	private BidDTO lastBid;

	public RefreshLoteDTO(Lote lote, BidDTO lastBidDTO) {
		super();
		this.loteId = lote.getId();
		this.endDate = lote.getEndDate();
		this.lastBid = lastBidDTO;
	}

	public Long getLoteId() {
		return loteId;
	}

	public void setLoteId(Long loteId) {
		this.loteId = loteId;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}

	public BidDTO getLastBid() {
		return lastBid;
	}

	public void setLastBid(BidDTO lastBid) {
		this.lastBid = lastBid;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RefreshLoteDTO [loteId=");
		builder.append(loteId);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", lastBid=");
		builder.append(lastBid);
		builder.append("]");
		return builder.toString();
	}

}
