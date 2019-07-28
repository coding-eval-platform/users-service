package ar.edu.itba.cep.users_service.security;

import ar.edu.itba.cep.users_service.security.authentication.anonymous.AnonymousAccessFilter;
import ar.edu.itba.cep.users_service.security.authentication.bearer_token.BearerTokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;

/**
 * Configuration class for security aspects at the web layer.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * A {@link BearerTokenAuthenticationFilter} to allow authentication with a Bearer token.
     */
    private final BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter;

    /**
     * An {@link AnonymousAccessFilter} that will intercept the request in case there is no
     * {@link org.springframework.security.core.Authentication} in the
     * {@link org.springframework.security.core.context.SecurityContext}.
     */
    private final AnonymousAccessFilter anonymousAccessFilter;


    /**
     * Constructor.
     *
     * @param bearerTokenAuthenticationFilter A {@link BearerTokenAuthenticationFilter}
     *                                        to allow authentication with a Bearer token.
     * @param anonymousAccessFilter           An {@link AnonymousAccessFilter} that will intercept the request
     *                                        in case there is no
     *                                        {@link org.springframework.security.core.Authentication} in the
     *                                        {@link org.springframework.security.core.context.SecurityContext}.
     */
    @Autowired
    public WebSecurityConfig(
            final BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter,
            final AnonymousAccessFilter anonymousAccessFilter) {
        this.bearerTokenAuthenticationFilter = bearerTokenAuthenticationFilter;
        this.anonymousAccessFilter = anonymousAccessFilter;
    }


    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .csrf()
                .disable()
            .cors()
                .disable()
            .formLogin()
                .disable()
            .httpBasic()
                .disable()
            .rememberMe()
                .disable()
            .logout()
                .disable()
            .anonymous()
                .disable()

            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()

            .addFilterBefore(bearerTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(anonymousAccessFilter, AnonymousAuthenticationFilter.class)

            .exceptionHandling()
                .authenticationEntryPoint((req, res, ex) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                .accessDeniedHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_FORBIDDEN))
            .and()
        ;
    }


    /**
     * Creates a {@link FilterRegistrationBean} of {@link BearerTokenAuthenticationFilter} to indicate
     * that the {@code bearerTokenAuthenticationFilter} must not be registered with the servlet container, as it is
     * part of the {@link org.springframework.security.web.SecurityFilterChain}
     * configured by this {@link Configuration} class.
     *
     * @return The {@link FilterRegistrationBean}.
     */
    @Bean
    public FilterRegistrationBean<BearerTokenAuthenticationFilter> bearerTokenAuthenticationFilter2Registration() {
        return disabledFilterRegistration(bearerTokenAuthenticationFilter);
    }

    /**
     * Creates a {@link FilterRegistrationBean} of {@link BearerTokenAuthenticationFilter} to indicate
     * that the {@code bearerTokenAuthenticationFilter} must not be registered with the servlet container, as it is
     * part of the {@link org.springframework.security.web.SecurityFilterChain}
     * configured by this {@link Configuration} class.
     *
     * @return The {@link FilterRegistrationBean}.
     */
    @Bean
    public FilterRegistrationBean<AnonymousAccessFilter> anonymousAccessFilterRegistration() {
        return disabledFilterRegistration(anonymousAccessFilter);
    }

    /**
     * Generic helper method to create a {@link FilterRegistrationBean} that indicates that the given {@code filter}
     * must not be registered with the servlet container.
     *
     * @param filter The {@link Filter} that must not be registered with the servlet container.
     * @param <F>    The concrete type of the {@code filter}.
     * @return The created {@link FilterRegistrationBean}.
     */
    private static <F extends Filter> FilterRegistrationBean<F> disabledFilterRegistration(final F filter) {
        final var registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
