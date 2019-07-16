package ar.edu.itba.cep.users_service.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;

/**
 * Security configuration class.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.users_service.security"
})
public class SecurityConfig {

    /**
     * Creates a bean of a {@link KeyFactory}.
     *
     * @return A {@link KeyFactory} bean.
     * @throws NoSuchAlgorithmException Never.
     */
    @Bean
    public KeyFactory rsaKeyFactory() throws NoSuchAlgorithmException {
        return KeyFactory.getInstance("RSA");
    }
}
