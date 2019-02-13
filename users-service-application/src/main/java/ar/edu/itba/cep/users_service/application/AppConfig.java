package ar.edu.itba.cep.users_service.application;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Main configuration class.
 */
@Configuration
@ComponentScan(value = {
        "ar.edu.itba.cep.users_service.*.config"
})
public class AppConfig {
}
