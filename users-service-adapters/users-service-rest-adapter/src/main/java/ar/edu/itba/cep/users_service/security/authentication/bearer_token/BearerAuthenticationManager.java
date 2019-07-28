package ar.edu.itba.cep.users_service.security.authentication.bearer_token;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * An {@link AuthenticationManager} that can process {@link PreAuthenticatedBearerToken}s using a {@link TokenDecoder}.
 */
@Component
public class BearerAuthenticationManager implements AuthenticationManager {

    /**
     * The {@link TokenDecoder} used to process the raw token that is included in a {@link PreAuthenticatedBearerToken}.
     */
    private final TokenDecoder tokenDecoder;


    /**
     * Constructor.
     *
     * @param tokenDecoder The {@link TokenDecoder}
     *                     used to process the raw token that is included in a {@link PreAuthenticatedBearerToken}.
     */
    public BearerAuthenticationManager(final TokenDecoder tokenDecoder) {
        this.tokenDecoder = tokenDecoder;
    }


    /**
     * {@inheritDoc}.
     *
     * @param authentication The authentication request object.
     *                       Must be an instance of {@link PreAuthenticatedBearerToken}.
     * @return A fully authenticated {@link BearerTokenAuthentication}.
     * @throws BearerTokenAuthenticationException If the raw token cannot be processed.
     * @throws IllegalArgumentException           In case the given {@code authentication} is {@code null}
     *                                            or is not a subclass of {@link PreAuthenticatedBearerToken}.
     */
    @Override
    public Authentication authenticate(final Authentication authentication)
            throws BearerTokenAuthenticationException, IllegalArgumentException {
        Assert.notNull(authentication, "The authentication must not be null");
        Assert.isAssignable(PreAuthenticatedBearerToken.class, authentication.getClass());
        final var bearerTokenAuthentication = Optional.of(authentication)
                .map(PreAuthenticatedBearerToken.class::cast)
                .map(PreAuthenticatedBearerToken::getRawToken)
                .flatMap(tokenDecoder::decode)
                .orElseThrow(() -> new BearerTokenAuthenticationException("Token could not be decoded"));
        bearerTokenAuthentication.authenticate();
        return bearerTokenAuthentication;
    }
}
