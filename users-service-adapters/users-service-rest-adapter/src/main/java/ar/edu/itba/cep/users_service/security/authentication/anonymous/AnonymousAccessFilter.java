package ar.edu.itba.cep.users_service.security.authentication.anonymous;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link javax.servlet.Filter} that sets an {@link AnonymousAccess} instance in the {@link SecurityContext}
 * if the request is not yet authenticated.
 */
@Component
public class AnonymousAccessFilter extends GenericFilterBean {


    @Override
    public void doFilter(
            final ServletRequest request,
            final ServletResponse response,
            final FilterChain chain) throws IOException, ServletException {
        processAnonymous();
        chain.doFilter(request, response);
    }


    /**
     * Performs the anonymous authentication process, checking whether the {@link SecurityContext} already holds an
     * {@link org.springframework.security.core.Authentication} instance.
     * If no {@link org.springframework.security.core.Authentication} is set in the {@link SecurityContext}, then
     * an {@link AnonymousAccess} instance will be set.
     *
     * @implNote This method verifies {@code SecurityContextHolder.getContext().getAuthentication() == null}
     * to check if the request is authenticated.
     */
    private static void processAnonymous() {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        Assert.state(Objects.nonNull(securityContext), "The SecurityContextHolder must hold a SecurityContext");

        Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .ifPresentOrElse(
                        auth -> {
                            // If present, do nothing
                        },
                        () -> securityContext.setAuthentication(AnonymousAccess.getInstance())
                )
        ;
    }
}
