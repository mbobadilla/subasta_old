package ar.com.pmp.subastados.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class BadBidException extends AbstractThrowableProblem {

	public BadBidException(String message) {
		super(null, message, Status.BAD_REQUEST);
	}
}
