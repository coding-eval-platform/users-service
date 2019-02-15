package ar.edu.itba.cep.users_service.domain.test_config;

import ar.edu.itba.cep.users_service.models.config.ModelsConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration class for the domain's module tests.
 */
@Configuration
@Import(ModelsConfig.class)
public class DomainTestConfig {
}
