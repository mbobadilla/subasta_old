package ar.com.pmp.subastados.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class EntityNotFoundException extends AbstractThrowableProblem {

	public EntityNotFoundException() {
		super(null, "Entity not found", Status.NOT_FOUND);
	}

	public EntityNotFoundException(String message) {
		super(null, message, Status.NOT_FOUND);
	}
}
