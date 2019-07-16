package ar.edu.itba.cep.users_service.security.authentication;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Class that wraps together an access token, and its associated refresh token.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class TokensWrapper {
    /**
     * The access token.
     */
    private final String accessToken;

    /**
     * The refresh token.
     */
    private final String refreshToken;
}
