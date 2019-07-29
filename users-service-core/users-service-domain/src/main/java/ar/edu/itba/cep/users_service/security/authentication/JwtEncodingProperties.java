package ar.edu.itba.cep.users_service.security.authentication;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Properties for configuring encoding of jwt tokens.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "authentication.jwt.users-service")
/* package */ class JwtEncodingProperties {

    /**
     * The private key.
     */
    private String privateKey;
    /**
     * The access token's duration, in seconds.
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration accessTokenDuration;
    /**
     * The refresh token's duration, in seconds.
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration refreshTokenDuration;
}
