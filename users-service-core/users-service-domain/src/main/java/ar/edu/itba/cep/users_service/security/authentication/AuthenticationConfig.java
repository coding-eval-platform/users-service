package ar.edu.itba.cep.users_service.security.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Base64Utils;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.util.function.Function;

/**
 * Configuration class for authentication.
 */
@Configuration
@EnableConfigurationProperties(JwtEncodingProperties.class)
public class AuthenticationConfig {

    /**
     * The {@link Logger}.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationConfig.class);


    /**
     * @return A {@link PasswordEncoder} to be used across the application,
     * that will be used to hash password before storing them.
     * @implNote The returned {@link PasswordEncoder} uses the BCrypt strong hashing function.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates a {@link TokenEncoder} bean.
     *
     * @param keyFactory            A {@link KeyFactory} used to create the {@link java.security.PrivateKey} needed by
     *                              the {@link JwtTokenEncoder}.
     * @param jwtEncodingProperties {@link JwtEncodingProperties} from where config data will be taken.
     * @return A {@link TokenEncoder} bean (i.e specifically, a {@link JwtTokenEncoder}).
     */
    @Bean
    public TokenEncoder jwtTokenEncoder(
            final KeyFactory keyFactory,
            final JwtEncodingProperties jwtEncodingProperties) {
        final var privateKey = generateKey(
                keyFactory,
                jwtEncodingProperties.getPrivateKey(),
                PKCS8EncodedKeySpec::new,
                KeyFactory::generatePrivate
        );
        final var accessTokenDuration = Duration.ofSeconds(jwtEncodingProperties.getAccessTokenDuration());
        final var refreshTokenDuration = Duration.ofSeconds(jwtEncodingProperties.getRefreshTokenDuration());

        if (refreshTokenDuration.compareTo(accessTokenDuration) <= 0) {
            LOGGER.error("The refresh token duration must be greater than the access token duration");
            throw new IllegalStateException("Invalid durations");
        }

        return new JwtTokenEncoder(
                privateKey,
                accessTokenDuration,
                refreshTokenDuration
        );
    }


    /**
     * Generates a {@link Key} of type {@code K} from the given {@link KeySpec} of type {@code S},
     * using the given {@link KeyFactory}. Generates a {@link Key} of type {@code K} from the given
     * {@code encodedKey}.
     *
     * @param keyFactory       The {@link KeyFactory} used to generate the {@link Key}.
     * @param encodedKey       The encoded key.
     * @param keySpecGenerator A {@link Function} that given a {@code byte[]}, returns a {@link
     *                         KeySpec} of type {@code S}. Will be called with the decoded version of the given {@code
     *                         encodedKey}.
     * @param keyGenerator     A {@link KeyGenerator}
     * @param <S>              The concrete type of {@link KeySpec}.
     * @param <K>              The concrete type of {@link Key}.
     * @return The generated {@link Key}.
     * @throws IllegalStateException If the key is invalid.
     * @implNote Will use {@link Base64Utils#decodeFromString(String)} to decode the given {@code
     * encodedKey}, and its result will be used to call the given {@code keySpecGenerator}.
     */
    private static <S extends KeySpec, K extends Key> K generateKey(
            final KeyFactory keyFactory,
            final String encodedKey,
            final Function<byte[], S> keySpecGenerator,
            final KeyGenerator<S, K> keyGenerator) {

        final var decodedKeyString = Base64Utils.decodeFromString(encodedKey);
        final var keySpec = keySpecGenerator.apply(decodedKeyString);

        try {
            return keyGenerator.generateKey(keyFactory, keySpec);
        } catch (final InvalidKeySpecException e) {
            LOGGER.error("The key that was set is not valid!");
            throw new IllegalStateException("Invalid key", e);
        }
    }

    /**
     * Defines behaviour for an object that can generate {@link Key}s from a {@link KeyFactory} and
     * an {@link KeySpec}
     *
     * @param <S> The concrete type of {@link KeySpec}.
     * @param <K> The concrete type of {@link Key}.
     */
    @FunctionalInterface
    interface KeyGenerator<S extends KeySpec, K extends Key> {

        /**
         * Generates a {@link Key} of type {@code K} using the given {@code keyFactory} and {@code
         * keySpec}.
         *
         * @param keyFactory The {@link KeyFactory} used to generate the {@link Key}.
         * @param keySpec    The {@link KeySpec} from where the {@link Key} will be created.
         * @return The generated {@link Key}.
         */
        K generateKey(final KeyFactory keyFactory, final S keySpec) throws InvalidKeySpecException;
    }
}
