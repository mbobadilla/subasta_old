package ar.com.pmp.subastados.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class LoginNotFoundException extends AbstractThrowableProblem {

    public LoginNotFoundException() {
        super(ErrorConstants.LOGIN_NOT_FOUND_TYPE, "User not registered", Status.BAD_REQUEST);
    }
}
