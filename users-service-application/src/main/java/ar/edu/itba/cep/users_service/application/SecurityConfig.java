package ar.edu.itba.cep.users_service.application;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Security configuration class.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.users_service.security"
})
public class SecurityConfig {
}
