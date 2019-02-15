package ar.edu.itba.cep.users_service.models.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration class for the model's module.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.users_service.models"
})
@Import(ValidationConfig.class)
public class ModelsConfig {
}
