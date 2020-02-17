package ar.com.pmp.subastados.events.dto;

import java.util.List;

import ar.com.pmp.subastados.domain.Bid;
import ar.com.pmp.subastados.domain.User;

public class BidNotificationDTO {

	private Bid bid;
	private List<User> followers;

	public BidNotificationDTO(Bid bid, List<User> followers) {
		super();
		this.bid = bid;
		this.followers = followers;
	}

	public Bid getBid() {
		return bid;
	}

	public void setBid(Bid bid) {
		this.bid = bid;
	}

	public List<User> getFollowers() {
		return followers;
	}

	public void setFollowers(List<User> followers) {
		this.followers = followers;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BidNotificationDTO [bid=");
		builder.append(bid);
		builder.append(", followers=");
		builder.append(followers);
		builder.append("]");
		return builder.toString();
	}

}
