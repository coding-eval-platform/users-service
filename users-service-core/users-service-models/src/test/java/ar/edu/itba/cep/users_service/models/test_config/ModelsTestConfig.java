package ar.edu.itba.cep.users_service.models.test_config;

import com.bellotapps.webapps_commons.validation.config.EnableValidationAspects;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

/**
 * Configuration class for the model's module tests.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.users_service.models"
})
@EnableValidationAspects
@EnableSpringConfigured
public class ModelsTestConfig {
}
