package ar.edu.itba.cep.users_service.security.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for authentication.
 */
@Configuration
public class AuthenticationConfig {

    /**
     * @return A {@link PasswordEncoder} to be used across the application,
     * that will be used to hash password before storing them.
     * @implNote The returned {@link PasswordEncoder} uses the BCrypt strong hashing function.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
