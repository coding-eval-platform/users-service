package ar.edu.itba.cep.users_service.security.authentication;

import ar.edu.itba.cep.users_service.models.AuthToken;

/**
 * Defines behaviour for an object that can encode {@link AuthToken}s.
 */
public interface TokenEncoder {


    /**
     * Encodes the given {@code authToken}.
     *
     * @param authToken The {@link AuthToken} to be encoded.
     * @return A {@link TokensWrapper} containing the access token and its associated refresh token that result from
     * encoding the given {@code authToken}.
     */
    TokensWrapper encode(final AuthToken authToken);
}
