package ar.edu.itba.cep.users_service.security.authentication;

import ar.edu.itba.cep.users_service.models.AuthToken;
import io.jsonwebtoken.Jwts;
import org.springframework.util.Assert;

import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

/**
 * A {@link TokenEncoder} that uses the jwt spec.
 */
class JwtTokenEncoder implements TokenEncoder {

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
     * @param privateKey           The {@link PrivateKey} used to sign tokens.
     * @param accessTokenDuration  The {@link Duration} of an access token.
     * @param refreshTokenDuration The {@link Duration} of a refresh token.
     */
    JwtTokenEncoder(
            final PrivateKey privateKey,
            final Duration accessTokenDuration,
            final Duration refreshTokenDuration) {
        this.privateKey = privateKey;
        this.accessTokenDuration = accessTokenDuration;
        this.refreshTokenDuration = refreshTokenDuration;
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
}
