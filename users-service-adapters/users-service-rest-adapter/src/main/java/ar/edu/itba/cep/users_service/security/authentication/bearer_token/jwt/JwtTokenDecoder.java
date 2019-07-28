package ar.edu.itba.cep.users_service.security.authentication.bearer_token.jwt;

import ar.edu.itba.cep.users_service.security.authentication.bearer_token.BearerTokenAuthentication;
import ar.edu.itba.cep.users_service.security.authentication.bearer_token.TokenDecoder;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.security.PublicKey;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * An implementation of a {@link TokenDecoder} using the JWT specification.
 */
@Component
public class JwtTokenDecoder implements TokenDecoder {

    /**
     * The roles claim.
     */
    private static final String ROLES_CLAIM = "roles";

    /**
     * The {@link PublicKey} used to verify external tokens.
     */
    private final PublicKey publicKey;
    /**
     * A {@link JwtHandlerAdapter} used to handle the decoding process.
     */
    private final CustomJwtHandlerAdapter jwtHandlerAdapter;


    /**
     * Constructor.
     *
     * @param publicKey The {@link PublicKey} used to verify external tokens.
     */
    @Autowired
    public JwtTokenDecoder(final PublicKey publicKey) {
        this.publicKey = publicKey;
        this.jwtHandlerAdapter = new CustomJwtHandlerAdapter();
    }


    @Override
    public Optional<BearerTokenAuthentication> decode(final String rawToken) {
        Assert.hasText(rawToken, "The token must not be null or empty");
        return Optional.of(rawToken)
                .flatMap(this::parseToken)
                .map(Jws::getBody)
                .map(JwtTokenDecoder::buildFromClaims);
    }


    /**
     * Parses the given {@code token}.
     *
     * @param token The token to be pared.
     * @return An {@link Optional} containing the parsed {@link Jws} of {@link Claims} if the given {@code rawToken}
     * could be parsed with no problems, or empty otherwise.
     * @apiNote This method also verifies the signature of the token.
     */
    private Optional<Jws<CustomBody>> parseToken(final String token) {
        try {
            final var jws = Jwts.parser().setSigningKey(publicKey).parse(token, jwtHandlerAdapter);
            return Optional.ofNullable(jws);
        } catch (final JwtException e) {
            return Optional.empty();
        }
    }

    /**
     * Builds a {@link BearerTokenAuthentication} from the given {@code claims}.
     *
     * @param claims The {@link CustomBody} from where the data is taken.
     * @return A {@link BearerTokenAuthentication} built with the data taken from the given {@code claims}.
     */
    private static BearerTokenAuthentication buildFromClaims(final CustomBody claims) {
        return new BearerTokenAuthentication(
                claims.getId(),
                claims.getUsername(),
                claims.getRoles()
        );
    }


    /**
     * A custom {@link JwtHandlerAdapter} of {@link Jws} of {@link CustomBody}.
     * Note that this {@link JwtHandlerAdapter} will validate the token.
     */
    private static final class CustomJwtHandlerAdapter extends JwtHandlerAdapter<Jws<CustomBody>> {

        @Override
        public Jws<CustomBody> onClaimsJws(final Jws<Claims> jws) {
            final var body = new CustomBody(
                    extractId(jws),
                    extractUsername(jws),
                    extractRoles(jws)
            );

            return new CustomJws(jws.getHeader(), body, jws.getSignature());
        }

        /**
         * Extracts the token's id from the given {@code jws} in the form of {@link UUID}.
         *
         * @param jws The {@link Jws} from where the id will be taken.
         * @return The token's id (in {@link UUID} form).
         * @throws MissingClaimException If there is no id in the given {@code jws}.
         * @throws MalformedJwtException If the given {@code jws}'s {@link Claims#ID} claim cannot be parsed
         *                               into a {@link UUID} instance
         */
        private static UUID extractId(final Jws<Claims> jws) throws MissingClaimException, MalformedJwtException {
            final var claims = jws.getBody();
            final var id = Optional.ofNullable(claims.getId())
                    .filter(StringUtils::hasLength)
                    .orElseThrow(() -> new MissingClaimException(jws.getHeader(), claims, "Missing \"jti\" claim"));
            try {
                return UUID.fromString(id);
            } catch (final IllegalArgumentException e) {
                throw new MalformedJwtException("The \"jti\" claim must be a valid UUID", e);
            }
        }

        /**
         * Extracts the username from the given {@code jws}.
         *
         * @param jws The {@link Jws} from where the username will be taken.
         * @return The username.
         * @throws MissingClaimException If there is no username in the given {@code jws}
         *                               (i.e no {@link Claims#SUBJECT} claim).
         * @implNote This method checks the {@link Claims#SUBJECT} claim in order to get the username.
         */
        private static String extractUsername(final Jws<Claims> jws) throws MissingClaimException {
            final var claims = jws.getBody();
            return Optional.ofNullable(claims.getSubject())
                    .filter(StringUtils::hasLength)
                    .orElseThrow(() -> new MissingClaimException(jws.getHeader(), claims, "Missing \"sub\" claim"));
        }

        /**
         * Extracts the roles from the given {@code jws}.
         *
         * @param jws The {@link Jws} from where the roles will be taken.
         * @return The roles.
         * @throws MissingClaimException If there is no roles in the given {@code jws}
         *                               (i.e no {@link #ROLES_CLAIM} claim).
         * @implNote This method checks the {@link #ROLES_CLAIM} claim in order to get the roles.
         */
        private static Set<String> extractRoles(final Jws<Claims> jws) throws MissingClaimException {
            final var claims = jws.getBody();
            return Optional.ofNullable(claims.get(ROLES_CLAIM, Collection.class))
                    .map(collection -> ((Collection<?>) collection))
                    .orElseThrow(() -> new MissingClaimException(jws.getHeader(), claims, "Missing \"roles\" claim"))
                    .stream()
                    .filter(role -> role instanceof String)
                    .map(String.class::cast)
                    .collect(Collectors.toSet());
        }
    }


    /**
     * A custom implementation of a {@link Jws} that uses a {@link CustomBody} instance as its body.
     */
    @AllArgsConstructor
    private static final class CustomJws implements Jws<CustomBody> {

        /**
         * The {@link JwsHeader}.
         */
        private final JwsHeader header;
        /**
         * The {@link CustomBody}.
         */
        private final CustomBody body;
        /**
         * The signature.
         */
        private final String signature;


        @Override
        public JwsHeader getHeader() {
            return header;
        }

        @Override
        public CustomBody getBody() {
            return body;
        }

        @Override
        public String getSignature() {
            return signature;
        }
    }


    /**
     * A custom jwt body that only includes the token's id, the username (i.e the subject), and the roles assigned in
     * the token.
     */
    @Getter
    @AllArgsConstructor
    private static final class CustomBody {
        /**
         * The jwt id.
         */
        private final UUID id;
        /**
         * The username (i.e the subject).
         */
        private final String username;
        /**
         * The roles assigned in the token (this is a custom claim).
         */
        private final Set<String> roles;
    }
}
