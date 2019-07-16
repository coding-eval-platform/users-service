package ar.edu.itba.cep.users_service.services;

import ar.edu.itba.cep.users_service.models.AuthToken;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

/**
 * A Data Transfer Object that wraps token data, including its id, its expiration {@link Instant},
 * the {@link String} representation, and a refresh token.
 */
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(of = "id", doNotUseGetters = true)
public class RawTokenContainer {

    /**
     * The token's id.
     */
    private final UUID id;
    /**
     * The access token.
     */
    private final String accessToken;
    /**
     * A token to be used to refresh the access token.
     */
    private final String refreshToken;


    /**
     * Constructor.
     *
     * @param authToken    The {@link AuthToken} from where data is taken.
     * @param accessToken  The access token (in {@link String} format).
     * @param refreshToken A token to be used to refresh the access token (in {@link String} format).
     */
    public RawTokenContainer(
            final AuthToken authToken,
            final String accessToken,
            final String refreshToken) {

        this.id = authToken.getId();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
