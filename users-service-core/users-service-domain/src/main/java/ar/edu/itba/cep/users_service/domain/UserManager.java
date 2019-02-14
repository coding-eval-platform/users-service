package ar.edu.itba.cep.users_service.domain;

import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.repositories.UserRepository;
import ar.edu.itba.cep.users_service.services.UserService;
import com.bellotapps.webapps_commons.errors.UniqueViolationError;
import com.bellotapps.webapps_commons.exceptions.CustomConstraintViolationException;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.exceptions.UniqueViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Constructor.
     *
     * @param userRepository Repository for {@link User}s.
     */
    @Autowired
    public UserManager(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public Page<User> findMatching(final String username, final Boolean active, final Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");

    }

    @Override
    public Optional<User> getByUsername(final String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public User register(final String username, final String password)
            throws UniqueViolationException, CustomConstraintViolationException {
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
        userRepository.findByUsername(username).ifPresent(userRepository::delete);
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
        // TODO: create credentials
    }

    /**
     * {@link UniqueViolationError} to be used when a username is already taken.
     */
    private static final UniqueViolationError USERNAME_IN_USE =
            new UniqueViolationError("The username is already in use", "username");
}
