package ar.edu.itba.cep.users_service.rest.config;

import com.bellotapps.webapps_commons.config.EnableJerseyApplication;
import com.bellotapps.webapps_commons.exceptions.CustomConstraintViolationException;
import com.bellotapps.webapps_commons.validation.jersey.ConstraintViolationExceptionCreator;
import com.bellotapps.webapps_commons.validation.jersey.EnableJerseyValidation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class in charge of configuring web concerns.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.users_service.rest.controller"
})
@EnableJerseyApplication(basePackages = {
        "ar.edu.itba.cep.users_service.rest.controller.endpoints",
        "com.bellotapps.webapps_commons.data_transfer.jersey.providers",
}, errorHandlersPackages = {
        "com.bellotapps.webapps_commons.error_handlers",
})
@EnableJerseyValidation
public class WebConfig {

    @Bean
    public ConstraintViolationExceptionCreator constraintViolationExceptionCreator() {
        return CustomConstraintViolationException::new;
    }
}
