package ar.edu.itba.cep.users_service.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties to be used to create the first user of the platform.
 */
@Data
@ConfigurationProperties(prefix = "users-service.first-user")
public final class FirstUserConfigurationProperties {
    private String username;
    private String password;
}
