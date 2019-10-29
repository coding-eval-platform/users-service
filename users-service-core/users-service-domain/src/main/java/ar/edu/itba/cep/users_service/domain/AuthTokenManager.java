package ar.edu.itba.cep.users_service.domain;

import ar.edu.itba.cep.roles.Role;
import ar.edu.itba.cep.users_service.domain.events.UserDeactivatedEvent;
import ar.edu.itba.cep.users_service.domain.events.UserDeletedEvent;
import ar.edu.itba.cep.users_service.domain.events.UserEvent;
import ar.edu.itba.cep.users_service.domain.events.UserRoleRemovedEvent;
import ar.edu.itba.cep.users_service.models.*;
import ar.edu.itba.cep.users_service.repositories.*;
import ar.edu.itba.cep.users_service.security.authentication.TokenEncoder;
import ar.edu.itba.cep.users_service.services.AuthTokenService;
import ar.edu.itba.cep.users_service.services.RawTokenContainer;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.exceptions.UnauthenticatedException;
import com.bellotapps.webapps_commons.exceptions.UnauthorizedException;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static ar.edu.itba.cep.users_service.security.authentication.Constants.REFRESH_GRANT;

/**
 * Manager for {@link AuthTokenService}.
 */
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class AuthTokenManager implements AuthTokenService {

    private final UserRepository userRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final AuthTokenRepository<AuthToken> authTokenRepository;
    private final UserAuthTokenRepository userAuthTokenRepository;
    private final SubjectAuthTokenRepository subjectAuthTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenEncoder tokenEncoder;


    @Override
    @Transactional
    public RawTokenContainer issueTokenForUser(final String username, final String password) throws UnauthenticatedException {
        return userRepository
                .findByUsername(username)
                .filter(User::isActive) // Check if the user can login
                .filter(user -> validPassword(user, password))
                .map(UserAuthToken::forUser)
                .map(userAuthTokenRepository::save) // Save the token and use the saved instance from now on.
                .map(this::buildTokens)
                .orElseThrow(UnauthenticatedException::new)
                ;
    }

    @Override
    @Transactional
    public RawTokenContainer issueTokenForSubject(final String subject, final Set<Role> roles) {
        final var token = subjectAuthTokenRepository.save(new SubjectAuthToken(subject, roles));
        return buildTokens(token);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or (isFullyAuthenticated() and principal == #username)")
    public List<UserAuthToken> listUserTokens(final String username) throws NoSuchEntityException {
        final var user = userRepository.findByUsername(username).orElseThrow(NoSuchEntityException::new);
        return userAuthTokenRepository.getUserTokens(user);
    }

    @Override
    public List<SubjectAuthToken> listSubjectTokens(final String subject) throws NoSuchEntityException {
        return subjectAuthTokenRepository.getSubjectTokens(subject);
    }

    @Override
    @PreAuthorize("hasAuthority('" + REFRESH_GRANT + "') and @authTokenAuthorizationProvider.isOwner(#id, principal)")
    public RawTokenContainer refreshToken(final UUID id) throws UnauthorizedException {
        // TODO: check that the REFRESH role is set and permissions (user, token id, etc).
        return authTokenRepository.findById(id)
                .filter(AuthToken::isValid)
                // Should not happen, but just in case...
                .filter(t -> !(t instanceof UserAuthToken) || ((UserAuthToken) t).getUser().isActive())
                .map(this::buildTokens)
                .orElseThrow(UnauthorizedException::new);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or @authTokenAuthorizationProvider.isOwner(#id, principal)")
    public void blacklistToken(final UUID id) {
        authTokenRepository.findById(id).ifPresent(this::blacklistToken);
    }


    /**
     * An {@link EventListener} that can handle {@link UserRoleRemovedEvent}s.
     * It will blacklist all the {@link AuthToken} belonging to the {@link User} being affected,
     * that contains the {@link Role} being removed.
     *
     * @param userRoleRemovedEvent The {@link UserRoleRemovedEvent} being handled.
     * @throws IllegalArgumentException If the {@code userRoleRemovedEvent} is {@code null},
     *                                  or if it contains a {@code null} {@link User} or {@link Role}.
     */
    @Transactional
    @EventListener(
            classes = {
                    UserRoleRemovedEvent.class,
            }
    )
    public void removeAllUserTokensWithRole(final UserRoleRemovedEvent userRoleRemovedEvent)
            throws IllegalArgumentException {
        Assert.notNull(userRoleRemovedEvent, "The event is null");
        final var user = userRoleRemovedEvent.getUser();
        final var role = userRoleRemovedEvent.getRole();
        Assert.notNull(user, "The user in the event must not be null");
        Assert.notNull(role, "The role in the event must not be null");
        userAuthTokenRepository.getUserTokensWithRole(user, role).forEach(this::blacklistToken);
    }

    /**
     * An {@link EventListener} that can handle {@link UserDeactivatedEvent}s and {@link UserDeletedEvent}s.
     * It will blacklist all the {@link AuthToken} belonging to the {@link User} being affected
     * (indicated in the received event).
     *
     * @param userEvent The {@link UserEvent} being handled.
     * @throws IllegalArgumentException If the {@code userEvent} is {@code null},
     *                                  or if it contains a {@code null} {@link User}.
     */
    @Transactional
    @EventListener(
            classes = {
                    UserDeactivatedEvent.class,
                    UserDeletedEvent.class,
            }
    )
    public void removeAllUserTokens(final UserEvent userEvent) throws IllegalArgumentException {
        Assert.notNull(userEvent, "The event is null");
        final var user = userEvent.getUser();
        Assert.notNull(user, "The user in the event must not be null");
        userAuthTokenRepository.getUserTokens(userEvent.getUser()).forEach(this::blacklistToken);
    }


    /**
     * Checks whether the given {@code password} matches the given {@code user}'s password.
     *
     * @param user     The {@link User} whose password must be checked.
     * @param password The password to be evaluated.
     * @return {@code true} if the passwords match, or {@code false} otherwise.
     */
    private boolean validPassword(final User user, final String password) {
        return userCredentialRepository.findLastForUser(user)
                .map(UserCredential::getHashedPassword)
                .filter(hashedPassword -> passwordEncoder.matches(password, hashedPassword))
                .isPresent()
                ;
    }

    /**
     * Builds a {@link RawTokenContainer} for the given {@code authToken}.
     *
     * @param authToken The {@link AuthToken}.
     * @return The built {@link RawTokenContainer}.
     */
    private RawTokenContainer buildTokens(final AuthToken authToken) {
        final var wrapper = tokenEncoder.encode(authToken);
        return new RawTokenContainer(
                authToken,
                wrapper.getAccessToken(),
                wrapper.getRefreshToken()
        );
    }

    /**
     * Blacklist's the given {@code authToken}.
     *
     * @param authToken The {@link AuthToken} to be blacklisted.
     */
    private void blacklistToken(final AuthToken authToken) {
        // Perform only if the token is not valid.
        if (authToken.isValid()) {
            authToken.invalidate();
            authTokenRepository.save(authToken);
            // TODO: stream token blacklisted event.
        }
    }
}
