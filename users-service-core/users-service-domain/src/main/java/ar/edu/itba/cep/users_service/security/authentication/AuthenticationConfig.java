package ar.edu.itba.cep.users_service.security.authentication;

import ar.edu.itba.cep.users_service.commons.KeyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

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
     * Creates a {@link PrivateKey} from the {@link KeyFactory} and the {@link JwtEncodingProperties}.
     *
     * @return The {@link PrivateKey}.
     */
    @Bean
    public PrivateKey privateKey(final KeyFactory keyFactory, final JwtEncodingProperties jwtEncodingProperties) {
        try {
            return KeyHelper.generateKey(
                    keyFactory,
                    jwtEncodingProperties.getPrivateKey(),
                    PKCS8EncodedKeySpec::new,
                    KeyFactory::generatePrivate
            );
        } catch (final KeyHelper.InvalidKeyException e) {
            LOGGER.error("The key that was set is not valid!", e);
            throw e;
        }
    }

    /**
     * Creates a {@link JwtTokenEncoder.DurationsWrapper} bean.
     *
     * @param jwtEncodingProperties {@link JwtEncodingProperties} from where config data will be taken.
     * @return A {@link JwtTokenEncoder.DurationsWrapper} bean.
     */
    @Bean
    public JwtTokenEncoder.DurationsWrapper durationsWrapper(final JwtEncodingProperties jwtEncodingProperties) {
        return new JwtTokenEncoder.DurationsWrapper(
                jwtEncodingProperties.getAccessTokenDuration(),
                jwtEncodingProperties.getRefreshTokenDuration()
        );
    }
}
