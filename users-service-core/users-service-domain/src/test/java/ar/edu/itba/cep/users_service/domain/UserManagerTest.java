package ar.edu.itba.cep.users_service.domain;

import ar.edu.itba.cep.users_service.domain.events.UserDeactivatedEvent;
import ar.edu.itba.cep.users_service.domain.events.UserDeletedEvent;
import ar.edu.itba.cep.users_service.domain.events.UserRoleRemovedEvent;
import ar.edu.itba.cep.roles.Role;
import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.models.UserCredential;
import ar.edu.itba.cep.users_service.models.ValidationConstants;
import ar.edu.itba.cep.users_service.repositories.UserCredentialRepository;
import ar.edu.itba.cep.users_service.repositories.UserRepository;
import ar.edu.itba.cep.users_service.services.UserWithRoles;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.exceptions.UnauthorizedException;
import com.bellotapps.webapps_commons.exceptions.UniqueViolationException;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.mockito.Mockito.*;

/**
 * Test class for the {@link UserManager}.
 */
@ExtendWith(MockitoExtension.class)
class UserManagerTest {

    /**
     * The {@link UserRepository} that is injected to the {@link UserManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    private final UserRepository userRepository;
    /**
     * The {@link UserCredentialRepository} that is injected to the {@link UserManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    private final UserCredentialRepository userCredentialRepository;

    private final ApplicationEventPublisher publisher;
    /**
     * The {@link PasswordEncoder} that is injected into a {@link UserManager} that will be tested.
     * This reference is saved in order to configure its behaviour in each test.
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * The {@link UserManager} to be tested.
     */
    private final UserManager userManager;


    /**
     * Constructor.
     *
     * @param userRepository           A mocked {@link UserRepository}
     *                                 to be injected into a {@link UserManager} that will be tested.
     * @param userCredentialRepository A mocked {@link UserCredentialRepository}
     *                                 to be injected into a {@link UserManager} that will be tested.
     * @param publisher                A mocked {@link ApplicationEventPublisher}
     *                                 to be injected into a {@link UserManager} that will be tested.
     * @param passwordEncoder          A mocked {@link PasswordEncoder}
     *                                 to be injected into a {@link UserManager} that will be tested.
     */
    public UserManagerTest(
            @Mock(name = "userRepository") final UserRepository userRepository,
            @Mock(name = "userCredentialRepository") final UserCredentialRepository userCredentialRepository,
            @Mock(name = "publisher") final ApplicationEventPublisher publisher,
            @Mock(name = "passwordEncoder") final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.publisher = publisher;
        this.passwordEncoder = passwordEncoder;
        this.userManager = new UserManager(
                userRepository,
                userCredentialRepository,
                publisher,
                passwordEncoder
        );
    }


    /**
     * Tests that searching for a {@link User} that exists returns the expected {@link User}.
     */
    @Test
    void testSearchForUser() {
        final var username = generateAcceptedUsername();
        final var user = new User(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        Assertions.assertTrue(
                userManager.getByUsername(username).isPresent(),
                "The manager is returning an empty optional when the user exists."
        );
        Assertions.assertEquals(
                username,
                userManager.getByUsername(username).map(UserWithRoles::getUsername).get(),
                "The returned user's username does not match the one used to search."
        );
        verifyZeroInteractions(userCredentialRepository);
        verifyZeroInteractions(publisher);
    }

    /**
     * Tests that searching for a {@link User} that does not exist returns an empty {@link Optional}.
     */
    @Test
    void testSearchForNonExistenceUserReturnsEmptyOptional() {
        final var username = generateAcceptedUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Assertions.assertTrue(
                userManager.getByUsername(username).isEmpty(),
                "Searching for a user that does not exists is not returning an empty Optional."
        );
        verifyZeroInteractions(userCredentialRepository);
        verifyZeroInteractions(publisher);
    }

    /**
     * Tests that creating a {@link User} with a username that is not used does not fail,
     * and that {@link UserRepository#save(Object)} is called at least once (which effectively saves the user).
     */
    @Test
    void testUserIsCreatedIfUsernameIsUnique() {
        final var username = generateAcceptedUsername();
        final var password = generateAcceptedPassword();
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.save(any(User.class))).then(invocation -> invocation.getArguments()[0]);
        Assertions.assertDoesNotThrow(
                () -> userManager.register(username, password),
                "Registering a User throws an unexpected Exception"
        );
        verify(userRepository, times(1)).save(argThat(u -> u.getUsername().equals(username)));
        verify(passwordEncoder, only()).encode(password);
        verify(userCredentialRepository, only()).save(argThat(uc -> uc.getUser().getUsername().equals(username)));
        verifyZeroInteractions(publisher);
    }


    /**
     * Tests that username uniqueness is taken into account,
     * throwing a {@link UniqueViolationException} if the username is already in use.
     * It also checks that the {@link UserRepository#save(Object)} operation is not called at all.
     */
    @Test
    void testUsernameUniqueness() {
        final var username = generateAcceptedUsername();
        final var password = generateAcceptedPassword();
        when(userRepository.existsByUsername(username)).thenReturn(true);
        Assertions.assertThrows(
                UniqueViolationException.class,
                () -> userManager.register(username, password),
                "Creating a user with an already taken username does not fail."
        );
        verify(userRepository, never()).save(any(User.class));
        verifyZeroInteractions(userCredentialRepository);
        verifyZeroInteractions(publisher);
    }


    /**
     * Tests that password is changed by verifying interactions
     * with the {@code userCredentialRepository} and {@code passwordEncoder}.
     *
     * @param user       A mocked {@link User} (the one owning the given {@link UserCredential}).
     * @param credential a mocked {@link UserCredential} (the actual credential for the given {@link User},
     *                   which will be replaced with a new one).
     */
    @Test
    void testChangeOfPasswordSavesANewCredential(
            @Mock(name = "user") final User user,
            @Mock(name = "credential") final UserCredential credential) {
        final Function<CharSequence, String> hashing = CharSequence::toString;
        preparePasswordEncoder(hashing);
        final var username = generateAcceptedUsername();
        final var currentPassword = generateAcceptedPassword();
        when(credential.getHashedPassword()).thenReturn(hashing.apply(currentPassword));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userCredentialRepository.findLastForUser(user)).thenReturn(Optional.of(credential));
        when(user.getUsername()).thenReturn(username);
        final var newPassword = generateAcceptedPassword() + "another";
        Assertions.assertDoesNotThrow(
                () -> userManager.changePassword(username, currentPassword, newPassword),
                "Changing the password is failing."
        );
        verify(userCredentialRepository, times(1)).findLastForUser(user);
        verify(userCredentialRepository, times(1)).save(argThat(uc -> uc.getUser().getUsername().equals(username)));
        verifyNoMoreInteractions(userCredentialRepository);
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(passwordEncoder, times(1)).matches(currentPassword, hashing.apply(credential.getHashedPassword()));
        verifyNoMoreInteractions(passwordEncoder);
        verifyZeroInteractions(publisher);
    }

    /**
     * Tests that trying to change the password of a {@link User} using a wrong password
     * throws an {@link UnauthorizedException}.
     *
     * @param user       A mocked {@link User} (the one whose password is tried to be changed).
     * @param credential A mocked {@link UserCredential}
     *                   (the actual credential of the {@link User}, used to check if passwords match).
     */
    @Test
    void testChangeOfPasswordWithWrongPassword(
            @Mock(name = "user") final User user,
            @Mock(name = "credential") final UserCredential credential) {
        final Function<CharSequence, String> hashing = CharSequence::toString;
        preparePasswordEncoder(hashing);
        final var username = generateAcceptedUsername();
        final var currentPassword = generateAcceptedPassword();
        when(credential.getHashedPassword()).thenReturn(hashing.apply(currentPassword));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userCredentialRepository.findLastForUser(user)).thenReturn(Optional.of(credential));
        final var newPassword = generateAcceptedPassword() + "another";
        final var wrongPassword = currentPassword + "Wrong!";
        Assertions.assertThrows(
                UnauthorizedException.class,
                () -> userManager.changePassword(username, wrongPassword, newPassword),
                "Changing the password with a wrong password is not throwing UnauthorizedException."
        );
        verify(userCredentialRepository, only()).findLastForUser(user);
        verify(passwordEncoder, only()).matches(wrongPassword, hashing.apply(credential.getHashedPassword()));
        verifyZeroInteractions(publisher);
    }

    /**
     * Tests that changing the password of a non existence {@link User} throws {@link NoSuchEntityException}.
     */
    @Test
    void testChangingPasswordToNonExistenceUserThrowsNoSuchEntityException() {
        final var currentPassword = generateAcceptedPassword();
        final var newPassword = currentPassword + "another";
        testNonExistenceActionThrowsNoSuchEntityException(
                (userManager, username) -> userManager.changePassword(username, currentPassword, newPassword),
                "Trying to change password to a user that does not exist is not throwing NoSuchEntityException."
        );
    }

    /**
     * Tests that adding a {@link Role} to a {@link User} works as expected.
     *
     * @param user A mocked {@link User} (the one to which a {@link Role} will be added).
     */
    @Test
    void testAddARole(@Mock(name = "user") final User user) {
        final var username = generateAcceptedUsername();
        final var role = getARole();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).then(invocation -> invocation.getArguments()[0]);
        doNothing().when(user).addRole(role);
        Assertions.assertDoesNotThrow(
                () -> userManager.addRole(username, role),
                "Adding a role throws an unexpected exception."
        );
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
        verify(user, only()).addRole(role);
        verifyZeroInteractions(publisher);
    }

    /**
     * Tests that removing a {@link Role} to a {@link User} works as expected.
     *
     * @param user A mocked {@link User} (the one to which a {@link Role} will be removed).
     */
    @Test
    void testRemoveARole(@Mock(name = "user") final User user) {
        final var username = generateAcceptedUsername();
        final var role = getARole();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).then(invocation -> invocation.getArguments()[0]);
        doNothing().when(user).removeRole(role);
        Assertions.assertDoesNotThrow(
                () -> userManager.removeRole(username, role),
                "Removing a role throws an unexpected exception."
        );
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
        verify(user, only()).removeRole(role);
        verify(publisher, only()).publishEvent(
                argThat(
                        (final UserRoleRemovedEvent e) -> e.getUser() == user && e.getRole() == role
                )
        );
    }


    /**
     * Tests that adding a {@link Role} to a non existence {@link User} throws {@link NoSuchEntityException}.
     */
    @Test
    void testAddRoleToNonExistenceUserThrowsNoSuchEntityException() {
        final var role = getARole();
        testNonExistenceActionThrowsNoSuchEntityException(
                (userManager, username) -> userManager.addRole(username, role),
                "Trying to add a role to a user that does not exist is not throwing NoSuchEntityException."
        );
    }

    /**
     * Tests that removing a {@link Role} from a non existence {@link User} throws {@link NoSuchEntityException}.
     */
    @Test
    void testRemoveRoleToNonExistenceUserThrowsNoSuchEntityException() {
        final var role = getARole();
        testNonExistenceActionThrowsNoSuchEntityException(
                (userManager, username) -> userManager.removeRole(username, role),
                "Trying to remove a role from a user that does not exist is not throwing NoSuchEntityException."
        );
    }

    /**
     * Tests that {@link User} activation through the {@link UserManager} actually activates the {@link User}
     * (checking directly with the said {@link User}, and checking by the returned {@link User} from the manager).
     * It also verifies that the {@link UserRepository#save(Object)} operation is actually being executed.
     */
    @Test
    void testUserActivation() {
        final var username = generateAcceptedUsername();
        final var user = new User(username);
        user.deactivate();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).then(invocation -> invocation.getArguments()[0]);
        userManager.activate(username);
        Assertions.assertTrue(
                user.isActive(),
                "Activating a user through the manager is not changing the real user"
        );
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(user);
        verifyZeroInteractions(userCredentialRepository);
        verifyZeroInteractions(publisher);
    }

    /**
     * Tests that {@link User} deactivation through the {@link UserManager} actually deactivates the {@link User}
     * (checking directly with the said {@link User}, and checking by the returned {@link User} from the manager).
     * It also verifies that the {@link UserRepository#save(Object)} operation is actually being executed.
     */
    @Test
    void testUserDeactivation() {
        final var username = generateAcceptedUsername();
        final var user = new User(username);
        user.activate(); // This is the default, but just in case...
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).then(invocation -> invocation.getArguments()[0]);
        userManager.deactivate(username);
        Assertions.assertFalse(
                user.isActive(),
                "Activating a user through the manager is not changing the real user"
        );
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(user);
        verifyZeroInteractions(userCredentialRepository);
        verify(publisher, only()).publishEvent(
                argThat(
                        (final UserDeactivatedEvent e) -> e.getUser() == user
                )
        );
        verifyZeroInteractions(publisher);
    }

    /**
     * Tests that activating a non existence {@link User} throws {@link NoSuchEntityException}.
     */
    @Test
    void testActivatingNonExistenceUserThrowsNoSuchEntityException() {
        testNonExistenceActionThrowsNoSuchEntityException(
                UserManager::activate,
                "Trying to activate a user that does not exist is not throwing NoSuchEntityException."
        );
    }

    /**
     * Tests that deactivating a non existence {@link User} throws {@link NoSuchEntityException}.
     */
    @Test
    void testDeactivatingNonExistenceUserThrowsNoSuchEntityException() {
        testNonExistenceActionThrowsNoSuchEntityException(
                UserManager::deactivate,
                "Trying to deactivate a user that does not exist is not throwing NoSuchEntityException."
        );
    }

    /**
     * Tests that deleting a {@link User} that exists does not fail, and that {@link UserRepository#delete(Object)}
     * is called at least once (which effectively deletes the user).
     */
    @Test
    void testDeletingAUserDoesNotFail() {
        final var username = generateAcceptedUsername();
        final var user = new User(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        Assertions.assertDoesNotThrow(
                () -> userManager.delete(username),
                "Deleting a user is failing."
        );
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).delete(user);
        verifyNoMoreInteractions(userRepository);
        verify(userCredentialRepository, only()).deleteByUser(user);
        verify(publisher, only()).publishEvent(
                argThat(
                        (final UserDeletedEvent e) -> e.getUser() == user
                )
        );
    }

    /**
     * Tests that deleting a {@link User} that does not exist does not fail,
     * and that {@link UserRepository#delete(Object)} is not called at all.
     */
    @Test
    void testDeletingNonExistenceUserDoesNotFail() {
        final var username = generateAcceptedUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Assertions.assertDoesNotThrow(
                () -> userManager.delete(username),
                "Deleting a user is failing."
        );
        verify(userRepository, only()).findByUsername(username);
        verifyZeroInteractions(userCredentialRepository);
        verifyZeroInteractions(publisher);
    }


    /**
     * Performs a test using the given {@code userManagerAction}
     * (which takes the {@link UserManager} and a username and performs an action on the first one),
     * asserting whether a {@link NoSuchEntityException} is thrown when there is no {@link User} with the said username.
     *
     * @param userManagerAction A {@link BiConsumer} of {@link UserManager} and {@link String},
     *                          which takes the {@code userManager} and a username,
     *                          and performs an action over the first one.
     * @param message           The message to be displayed in case the test fails.
     */
    private void testNonExistenceActionThrowsNoSuchEntityException(
            final BiConsumer<UserManager, String> userManagerAction,
            final String message) {
        final var username = generateAcceptedUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                NoSuchEntityException.class,
                () -> userManagerAction.accept(userManager, username), message
        );
        verifyZeroInteractions(userCredentialRepository);
        verifyZeroInteractions(publisher);
    }

    /**
     * Prepares the {@code passwordEncoder} mock with the given {@code encoderFunction}.
     *
     * @param encoderFunction The encoding {@link Function}.
     */
    private void preparePasswordEncoder(final Function<CharSequence, String> encoderFunction) {
        when(passwordEncoder.encode(anyString())).then(i -> encoderFunction.apply(i.getArgument(0)));
        when(passwordEncoder.matches(anyString(), anyString()))
                .then(i -> encoderFunction.apply(i.getArgument(0)).equals(i.getArgument(1)));
    }

    /**
     * @return A random username whose length is between the valid limits.
     */
    private static String generateAcceptedUsername() {
        return Faker.instance()
                .lorem()
                .fixedString(ValidationConstants.USERNAME_MAX_LENGTH)
                .replaceAll("\\s+", "a"); // Changes all white spaces into any character
    }

    /**
     * @return A password that is valid.
     */
    private static String generateAcceptedPassword() {
        return "Some Password 1!";
    }

    /**
     * @return A random {@link Role}.
     */
    private static Role getARole() {
        final var roles = Role.values();
        final var index = (int) Faker.instance().number().numberBetween(0L, roles.length);
        return roles[index];
    }

    /**
     * A {@link FunctionalInterface} for operations over {@link User}s involving a {@link Role},
     * through a {@link UserManager}
     */
    @FunctionalInterface
    private interface RoleOperation {
        /**
         * Performs an operation over a {@link User} with the given {@code username}, involving the given {@code role},
         * through the given {@link UserManager}.
         *
         * @param userManager The {@link UserManager} through which the operation is performed.
         * @param username    The username of the {@link User} being operated.
         * @param role        The {@link Role} involved in the operation.
         */
        void operate(final UserManager userManager, final String username, final Role role);
    }
}
