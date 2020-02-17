package ar.com.pmp.subastados.security;

import org.springframework.security.core.AuthenticationException;

/**
 * This exception is thrown in case of a blocked user trying to authenticate.
 */
public class UserBlockedException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public UserBlockedException(String message) {
        super(message);
    }

    public UserBlockedException(String message, Throwable t) {
        super(message, t);
    }
}
