package ar.edu.itba.cep.users_service.spring_data.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration class for Spring Data Jpa Repositories.
 */
@Configuration
@EnableJpaRepositories(basePackages = {
        "ar.edu.itba.cep.users_service.repositories"
})
@EntityScan(basePackages = {
        "ar.edu.itba.cep.users_service.models"
})
public class SpringDataConfig {
}
