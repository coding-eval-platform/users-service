package ar.edu.itba.cep.users_service.domain.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the domain's module.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.users_service.domain"
})
@EnableConfigurationProperties(FirstUserConfigurationProperties.class)
public class DomainConfig {
}
