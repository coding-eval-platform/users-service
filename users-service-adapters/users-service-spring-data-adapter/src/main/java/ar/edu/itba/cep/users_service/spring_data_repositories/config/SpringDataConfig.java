package ar.edu.itba.cep.users_service.spring_data_repositories.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration class for Spring Data Jpa Repositories.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.users_service.spring_data_repositories"
})
@EnableJpaRepositories(basePackages = {
        "ar.edu.itba.cep.users_service.spring_data_repositories.spring_data_interfaces"
})
@EntityScan(basePackages = {
        "ar.edu.itba.cep.users_service.models"
})
public class SpringDataConfig {
}
