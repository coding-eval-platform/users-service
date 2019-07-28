package ar.edu.itba.cep.users_service.security.authentication.bearer_token;

import java.util.Optional;

/**
 * Defines behaviour for an object that can decode a {@link BearerTokenAuthentication} from a {@link String}.
 */
public interface TokenDecoder {

    /**
     * Decodes the given {@code rawToken}.
     *
     * @param rawToken The raw token to be decoded.
     * @return An {@link Optional} of {@link BearerTokenAuthentication} if the token can be decoded, or empty otherwise.
     */
    Optional<BearerTokenAuthentication> decode(final String rawToken);
}
