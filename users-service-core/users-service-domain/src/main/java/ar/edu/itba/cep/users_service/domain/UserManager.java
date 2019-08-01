package ar.edu.itba.cep.users_service.domain;

import ar.edu.itba.cep.users_service.models.Role;
import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.models.UserCredential;
import ar.edu.itba.cep.users_service.repositories.UserCredentialRepository;
import ar.edu.itba.cep.users_service.repositories.UserRepository;
import ar.edu.itba.cep.users_service.services.UserService;
import ar.edu.itba.cep.users_service.services.UserWithNoRoles;
import ar.edu.itba.cep.users_service.services.UserWithRoles;
import com.bellotapps.webapps_commons.errors.UniqueViolationError;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.exceptions.UnauthorizedException;
import com.bellotapps.webapps_commons.exceptions.UniqueViolationException;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Manager for {@link User}s.
 */
@Service
@Transactional(readOnly = true)
public class UserManager implements UserService, InitializingBean {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserManager.class);


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
    @Transactional
    public void afterPropertiesSet() {
        createAdminUser();
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<UserWithNoRoles> findMatching(
            final String username,
            final Boolean active,
            final PagingRequest pagingRequest) {
        return userRepository.findFiltering(username, active, pagingRequest)
                .map(UserWithNoRoles::new);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or principal == #username")
    public Optional<UserWithRoles> getByUsername(final String username) {
        return findWithRoles(username);
    }

    @Override
    @PreAuthorize("isFullyAuthenticated()")
    public Optional<UserWithRoles> getActualUser() {
        final var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!ClassUtils.isAssignable(String.class, principal.getClass())) {
            throw new RuntimeException("The authentication principal must be a String!");
        }
        return findWithRoles((String) principal);
    }


    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserWithNoRoles register(final String username, final String password)
            throws UniqueViolationException, IllegalArgumentException {
        // First check if the username is already in use.
        if (userRepository.existsByUsername(username)) {
            throw new UniqueViolationException(List.of(USERNAME_IN_USE));
        }

        final User user = userRepository.save(new User(username)); // Create a new User, and save it.
        createCredential(user, password); // Then, create the initial credential for it
        return new UserWithNoRoles(user);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or principal == #username")
    public void changePassword(
            final String username,
            final String currentPassword,
            final String newPassword) throws NoSuchEntityException, UnauthorizedException, IllegalArgumentException {
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
    @PreAuthorize("hasAuthority('ADMIN')")
    public void addRole(final String username, final Role role)
            throws NoSuchEntityException, IllegalArgumentException {
        operateOverUserWithUsername(username, user -> user.addRole(role));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void removeRole(final String username, final Role role) throws NoSuchEntityException {
        operateOverUserWithUsername(username, user -> user.removeRole(role));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void activate(final String username) throws NoSuchEntityException {
        operateOverUserWithUsername(username, User::activate);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deactivate(final String username) throws NoSuchEntityException {
        operateOverUserWithUsername(username, User::deactivate);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(final String username) {
        userRepository.findByUsername(username).ifPresent(this::deleteUser);
    }


    /**
     * Searches for the {@link User} with the given {@code username}, wrapping it in a {@link UserWithRoles} instance.
     *
     * @param username The username used to search.
     * @return An {@link Optional} with the {@link UserWithRoles} wrapping the {@link User}
     * with the given {@code username} if it exists, or {@code null} otherwise.
     */
    private Optional<UserWithRoles> findWithRoles(final String username) {
        return userRepository.findByUsername(username).map(user -> {
            user.getRoles().size(); // Initialize Lazy Collection
            return new UserWithRoles(user);
        });
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
     * Performs an operation over the {@link User} with the given {@code username}, and then saves
     * the {@link User}.
     *
     * @param username      The username.
     * @param userOperation A {@link Consumer} that takes a {@link User} (the one with the given
     *                      {@code username}), and performs an operation over it.
     * @throws NoSuchEntityException If there is no {@link User} with the given {@code username}.
     */
    private void operateOverUserWithUsername(
            final String username,
            final Consumer<User> userOperation) throws NoSuchEntityException {
        final var user = loadUser(username);
        userOperation.accept(user);
        userRepository.save(user);
    }

    /**
     * Creates a new credential for the given {@link User} using the given {@code password}.
     *
     * @param user     The {@link User} owning the new credential.
     * @param password The password for the credential.
     */
    private void createCredential(final User user, final String password) {
        Assert.notNull(user, "The user must not be null");
        final var credential = UserCredential
                .buildCredential(user, password, passwordEncoder::encode);
        userCredentialRepository.save(credential);
    }

    /**
     * Performs the operation of removing a {@link User} of the system, removing also all its {@link
     * UserCredential}.
     *
     * @param user The {@link User} to be deleted.
     */
    private void deleteUser(final User user) {
        userCredentialRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    /**
     * Creates an admin user if there is any.
     */
    private void createAdminUser() {
        if (!userRepository.existsWithRole(Role.ADMIN)) {
            LOGGER.info("No Admin user exists... Creating a new one");
            final var username = UUID.randomUUID().toString();
            final var randomString = UUID.randomUUID().toString();
            final var password = randomString.toLowerCase() + randomString.toUpperCase() + "1!";
            final var user = new User(username);
            user.addRole(Role.ADMIN);
            final User savedUser = userRepository.save(user);
            createCredential(savedUser, password);
            LOGGER.info("Created admin user with username {} and password {}", username, password);
        }
    }

    /**
     * {@link UniqueViolationError} to be used when a username is already taken.
     */
    private static final UniqueViolationError USERNAME_IN_USE =
            new UniqueViolationError("The username is already in use", "username");
}
