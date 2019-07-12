package ar.edu.itba.cep.users_service.domain;

import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.models.UserCredential;
import ar.edu.itba.cep.users_service.repositories.UserCredentialRepository;
import ar.edu.itba.cep.users_service.repositories.UserRepository;
import ar.edu.itba.cep.users_service.services.UserService;
import com.bellotapps.webapps_commons.errors.UniqueViolationError;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.exceptions.UnauthorizedException;
import com.bellotapps.webapps_commons.exceptions.UniqueViolationException;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

/**
 * Manager for {@link User}s.
 */
@Service
@Transactional(readOnly = true)
public class UserManager implements UserService {

    /**
     * Repository for {@link User}s.
     */
    private final UserRepository userRepository;

    /**
     * Repository for {@link UserCredential}s.
     */
    private final UserCredentialRepository userCredentialRepository;

    /**
     * {@link PasswordEncoder} used for hashing passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor.
     *
     * @param userRepository           Repository for {@link User}s.
     * @param userCredentialRepository Repository for {@link UserCredential}s.
     * @param passwordEncoder          {@link PasswordEncoder} used for hashing passwords.
     */
    @Autowired
    public UserManager(final UserRepository userRepository,
                       final UserCredentialRepository userCredentialRepository,
                       final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Page<User> findMatching(final String username, final Boolean active, final PagingRequest pagingRequest) {
        return userRepository.findFiltering(username, active, pagingRequest);
    }

    @Override
    public Optional<User> getByUsername(final String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public User register(final String username, final String password)
            throws UniqueViolationException, IllegalArgumentException {
        // First check if the username is already in use.
        if (userRepository.existsByUsername(username)) {
            throw new UniqueViolationException(List.of(USERNAME_IN_USE));
        }

        final User user = userRepository.save(new User(username)); // Create a new User, and save it.
        createCredential(user, password); // Then, create the initial credential for it

        return user;
    }

    @Override
    @Transactional
    public void changePassword(final String username, final String currentPassword, final String newPassword)
            throws NoSuchEntityException, UnauthorizedException, IllegalArgumentException {
        final var user = loadUser(username);
        final var actualCredential = userCredentialRepository.findLastForUser(user)
                .orElseThrow(() -> new RuntimeException("Invalid system state. This should not happen."));
        // Check that the currentPassword matches the actual password
        Optional.ofNullable(currentPassword)
                .filter(password -> passwordEncoder.matches(password, actualCredential.getHashedPassword()))
                .orElseThrow(() -> new UnauthorizedException("Passwords don't match"));
        // If reached here, passwords match. Change of password can be performed.
        createCredential(user, newPassword);
    }

    @Override
    @Transactional
    public void activate(final String username) throws NoSuchEntityException {
        final var user = loadUser(username);
        user.activate();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deactivate(final String username) throws NoSuchEntityException {
        final var user = loadUser(username);
        user.deactivate();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(final String username) {
        userRepository.findByUsername(username).ifPresent(this::deleteUser);
    }


    /**
     * Loads the {@link User} with the given {@code username} if it exists.
     *
     * @param username The username.
     * @return The {@link User} with the given {@code username}.
     * @throws NoSuchEntityException If there is no {@link User} with the given {@code username}.
     */
    private User loadUser(final String username) throws NoSuchEntityException {
        return userRepository.findByUsername(username).orElseThrow(NoSuchEntityException::new);
    }

    /**
     * Creates a new credential for the given {@link User} using the given {@code password}.
     *
     * @param user     The {@link User} owning the new credential.
     * @param password The password for the credential.
     */
    private void createCredential(final User user, final String password) {
        Assert.notNull(user, "The user must not be null");
        final var credential = UserCredential.buildCredential(user, password, passwordEncoder::encode);
        userCredentialRepository.save(credential);
    }

    /**
     * Performs the operation of removing a {@link User} of the system,
     * removing also all its {@link UserCredential}.
     *
     * @param user The {@link User} to be deleted.
     */
    private void deleteUser(final User user) {
        userCredentialRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    /**
     * {@link UniqueViolationError} to be used when a username is already taken.
     */
    private static final UniqueViolationError USERNAME_IN_USE =
            new UniqueViolationError("The username is already in use", "username");
}
