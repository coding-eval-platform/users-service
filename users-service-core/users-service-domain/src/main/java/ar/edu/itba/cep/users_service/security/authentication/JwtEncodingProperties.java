package ar.edu.itba.cep.users_service.security.authentication;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for configuring encoding of jwt tokens.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "authentication.jwt")
public class JwtEncodingProperties {

    /**
     * The private key.
     */
    private String privateKey;
    /**
     * The access token's duration, in seconds.
     */
    private Long accessTokenDuration;
    /**
     * The refresh token's duration, in seconds.
     */
    private Long refreshTokenDuration;
}
