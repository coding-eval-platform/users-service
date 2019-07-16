package ar.edu.itba.cep.users_service.domain;

import ar.edu.itba.cep.users_service.models.AuthToken;
import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.models.UserCredential;
import ar.edu.itba.cep.users_service.repositories.AuthTokenRepository;
import ar.edu.itba.cep.users_service.repositories.UserCredentialRepository;
import ar.edu.itba.cep.users_service.repositories.UserRepository;
import ar.edu.itba.cep.users_service.security.authentication.TokenEncoder;
import ar.edu.itba.cep.users_service.services.AuthTokenService;
import ar.edu.itba.cep.users_service.services.RawTokenContainer;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.exceptions.UnauthenticatedException;
import com.bellotapps.webapps_commons.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Manager for {@link AuthTokenService}.
 */
@Service
public class AuthTokenManager implements AuthTokenService {

    /**
     * A {@link UserRepository} used to search for {@link User}s when issuing tokens, and when listing tokens.
     */
    private final UserRepository userRepository;
    /**
     * A {@link UserCredentialRepository} used to search for the actual {@link UserCredential} of a {@link User}
     * when issuing tokens.
     */
    private final UserCredentialRepository userCredentialRepository;
    /**
     * The {@link AuthTokenRepository} that allows {@link AuthToken} persistence.
     */
    private final AuthTokenRepository authTokenRepository;
    /**
     * The {@link PasswordEncoder} used to match passwords.
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * The {@link TokenEncoder} used to encode an {@link AuthToken} into raw access and refresh tokens.
     */
    private final TokenEncoder tokenEncoder;


    /**
     * Constructor.
     *
     * @param userRepository           A {@link UserRepository} used to search for {@link User}s when issuing tokens,
     *                                 and when listing tokens.
     * @param userCredentialRepository A {@link UserCredentialRepository}
     *                                 used to search for the actual {@link UserCredential} of a {@link User}
     *                                 when issuing tokens.
     * @param authTokenRepository      The {@link AuthTokenRepository} that allows {@link AuthToken} persistence.
     * @param passwordEncoder          The {@link PasswordEncoder} used to match passwords.
     * @param tokenEncoder             The {@link TokenEncoder} used to encode an {@link AuthToken}
     *                                 into raw access and refresh tokens.
     */
    @Autowired
    public AuthTokenManager(
            final UserRepository userRepository,
            final UserCredentialRepository userCredentialRepository,
            final AuthTokenRepository authTokenRepository,
            final PasswordEncoder passwordEncoder,
            final TokenEncoder tokenEncoder) {
        this.userRepository = userRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.authTokenRepository = authTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenEncoder = tokenEncoder;
    }


    @Override
    public RawTokenContainer issueToken(final String username, final String password) throws UnauthenticatedException {
        return userRepository
                .findByUsername(username)
                .filter(user -> validPassword(user, password))
                .filter(User::isActive) // Check if the user can login
                .map(AuthToken::new)
                .map(authTokenRepository::save) // Save the token and use the saved instance from now on.
                .map(this::buildTokens)
                .orElseThrow(UnauthenticatedException::new)
                ;
    }

    @Override
    public RawTokenContainer refreshToken(final UUID id) throws UnauthorizedException {
        // TODO: check that the REFRESH role is set and permissions (user, token id, etc).
        return authTokenRepository.findById(id)
                .map(this::buildTokens)
                .orElseThrow(UnauthorizedException::new);
    }

    @Override
    public void blacklistToken(final UUID id) {
        authTokenRepository.findById(id)
                .ifPresent(authToken -> {
                    authToken.invalidate();
                    authTokenRepository.save(authToken);
                    // TODO: stream token blacklisted event.
                });
    }

    @Override
    public List<AuthToken> listTokens(final String username) throws NoSuchEntityException {
        return userRepository.findByUsername(username)
                .map(authTokenRepository::getUserTokens)
                .orElseThrow(NoSuchEntityException::new);
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
}
