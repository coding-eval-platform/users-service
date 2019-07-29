package ar.edu.itba.cep.users_service.security.authentication;

import ar.edu.itba.cep.users_service.models.AuthToken;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

/**
 * A {@link TokenEncoder} that uses the jwt spec.
 */
@Component
/* package */ class JwtTokenEncoder implements TokenEncoder {

    /**
     * The {@link PrivateKey} used to sign tokens.
     */
    private final PrivateKey privateKey;
    /**
     * The {@link Duration} of an access token.
     */
    private final Duration accessTokenDuration;
    /**
     * The {@link Duration} of a refresh token.
     */
    private final Duration refreshTokenDuration;


    /**
     * Constructor.
     *
     * @param privateKey       The {@link PrivateKey} used to sign tokens.
     * @param durationsWrapper A {@link DurationsWrapper} containing both the {@link Duration}
     *                         for the access and the refresh tokens.
     */
    /* package */ JwtTokenEncoder(
            final PrivateKey privateKey,
            final DurationsWrapper durationsWrapper) {
        this.privateKey = privateKey;
        this.accessTokenDuration = durationsWrapper.getAccessTokenDuration();
        this.refreshTokenDuration = durationsWrapper.getRefreshTokenDuration();
    }


    @Override
    public TokensWrapper encode(final AuthToken authToken) {
        Assert.notNull(authToken, "The token must not be null");
        final var now = Instant.now();
        final var accessToken = Jwts.builder()
                .setId(authToken.getId().toString())
                .setSubject(authToken.getUser().getUsername())
                .claim(Constants.ROLES_CLAIM, authToken.getRolesAssigned())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(accessTokenDuration)))
                .signWith(privateKey, Constants.SIGNATURE_ALGORITHM)
                .compact();
        final var refreshToken = Jwts.builder()
                .setId(authToken.getId().toString())
                .setSubject(authToken.getUser().getUsername())
                .claim(Constants.ROLES_CLAIM, Set.of(Constants.REFRESH_GRANT))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(refreshTokenDuration)))
                .signWith(privateKey, Constants.SIGNATURE_ALGORITHM)
                .compact();
        return new TokensWrapper(accessToken, refreshToken);
    }

    /**
     * A wrapper class that contains both the {@link Duration} for the access and the refresh tokens.
     */
    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode(doNotUseGetters = true)
    @ToString(doNotUseGetters = true)
    /* package */ static final class DurationsWrapper {

        /**
         * The {@link Duration} of an access token.
         */
        private final Duration accessTokenDuration;
        /**
         * The {@link Duration} of a refresh token.
         */
        private final Duration refreshTokenDuration;
    }
}
