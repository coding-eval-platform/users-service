package ar.edu.itba.cep.users_service.security.authentication.bearer_token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link javax.servlet.Filter} in charge of performing the Bearer token authentication process.
 */
@Component
public class BearerTokenAuthenticationFilter extends GenericFilterBean {

    /**
     * A {@link Logger}.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BearerTokenAuthenticationFilter.class);


    /**
     * A {@link BearerAuthenticationManager} to be passed to the superclass constructor.
     */
    private final BearerAuthenticationManager bearerAuthenticationManager;


    /**
     * Constructor.
     *
     * @param bearerAuthenticationManager A {@link BearerAuthenticationManager}
     *                                    to be passed to the superclass constructor.
     */
    @Autowired
    public BearerTokenAuthenticationFilter(final BearerAuthenticationManager bearerAuthenticationManager) {
        this.bearerAuthenticationManager = bearerAuthenticationManager;
    }


    @Override
    public void doFilter(
            final ServletRequest request,
            final ServletResponse response,
            final FilterChain chain) throws IOException, ServletException {
        Assert.state(
                Objects.nonNull(SecurityContextHolder.getContext()),
                "The SecurityContextHolder must hold a SecurityContext"
        );

        try {
            processAuthentication((HttpServletRequest) request);
        } catch (final Throwable e) {
            processThrowable((HttpServletResponse) response, e);
            return;
        }
        chain.doFilter(request, response);
    }


    /**
     * Performs the Bearer authentication process for the given {@code request},
     * making use of the {@link BearerAuthenticationManager}.
     *
     * @param request The {@link HttpServletRequest} to be processed.
     * @throws BearerTokenAuthenticationException If the {@code request}
     *                                            holds a Bearer token than cannot be authenticated.
     */
    private void processAuthentication(final HttpServletRequest request) throws BearerTokenAuthenticationException {
        extractJwtToken(request)
                .map(PreAuthenticatedBearerToken::new)
                .map(bearerAuthenticationManager::authenticate)
                .ifPresent(auth -> SecurityContextHolder.getContext().setAuthentication(auth));
    }

    /**
     * Performs the authentication error process, modifying the given {@code response} if needed.
     *
     * @param response The {@link HttpServletResponse} to be modified in case of an expected error.
     * @param e        The {@link Throwable} to be modified.
     */
    private static void processThrowable(final HttpServletResponse response, final Throwable e) {
        Assert.notNull(e, "The Throwable to be processed must not be null");
        if (e instanceof BearerTokenAuthenticationException) {
            // Only in this case we can make sure that the authentication process fails.
            // Any other exception is not handled by this filter.
            // Note that the tokens being processed are internal tokens, so this is an unusual situation.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            LOGGER.debug("An unexpected error occur while decoding an internal token. This should not happen");
            return;
        }
        if (e instanceof AuthenticationException) {
            LOGGER.warn("An unexpected AuthenticationException was thrown", e);
            return;
        }
        LOGGER.error("An unexpected Throwable was thrown", e);
    }

    /**
     * Extracts a JWT token from the given {@link HttpServletRequest}.
     *
     * @param request The {@link HttpServletRequest} from where the token will be extracted.
     * @return An {@link Optional} containing the JWT if it exists in the {@code request}, or empty
     * otherwise.
     * @implNote This method searches for the {@link Constants#BEARER_SCHEME} header in the
     * given {@code request}, which should contain the token with the following format:
     * Bearer&lt;space&gt;&lt;token&gt;.
     */
    private static Optional<String> extractJwtToken(final HttpServletRequest request) {
        Assert.notNull(request, "The request must not be null");
        return Optional.of(request)
                .map(req -> req.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(StringUtils::hasText)
                .map(header -> header.split(" "))
                .filter(splitted -> splitted.length == 2)
                .filter(splitted -> Constants.BEARER_SCHEME.equals(splitted[0]))
                .map(splitted -> splitted[1])
                ;
    }
}
