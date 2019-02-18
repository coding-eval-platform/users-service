package ar.edu.itba.cep.users_service.rest.config;

import com.bellotapps.webapps_commons.config.EnableJerseyApplication;
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
public class WebConfig {
}
