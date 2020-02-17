package ar.com.pmp.subastados.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class BadRequestException extends AbstractThrowableProblem {

	public BadRequestException(String message) {
		super(null, message, Status.BAD_REQUEST);
	}
}
