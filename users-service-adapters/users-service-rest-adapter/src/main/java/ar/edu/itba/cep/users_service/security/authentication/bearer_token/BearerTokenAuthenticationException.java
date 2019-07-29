package ar.edu.itba.cep.users_service.security.authentication.bearer_token;

import org.springframework.security.core.AuthenticationException;

/**
 * An {@link AuthenticationException} to be thrown when there are bearer token issues.
 */
public class BearerTokenAuthenticationException extends AuthenticationException {

    /**
     * Constructs a {@code BearerTokenAuthenticationException} with the specified message and root cause.
     *
     * @param message the detail message.
     * @param cause   the root cause.
     */
    public BearerTokenAuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an {@code BearerTokenAuthenticationException} with the specified message and no root cause.
     *
     * @param message the detail message.
     */
    public BearerTokenAuthenticationException(final String message) {
        super(message);
    }
}
