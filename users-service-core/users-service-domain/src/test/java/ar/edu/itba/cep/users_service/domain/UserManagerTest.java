package ar.edu.itba.cep.users_service.domain;

import ar.edu.itba.cep.users_service.domain.test_config.DomainTestConfig;
import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.models.ValidationConstants;
import ar.edu.itba.cep.users_service.repositories.UserRepository;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.exceptions.UniqueViolationException;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Test class for the user manager.
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        DomainTestConfig.class
})
class UserManagerTest {

    /**
     * A mocked {@link UserRepository} that is injected to the {@link UserManager}.
     * This reference is saved in order to configure its behaviour in each test.
     */
    private final UserRepository userRepository;

    /**
     * The {@link UserManager} to be tested.
     */
    private final UserManager userManager;


    /**
     * Constructor.
     *
     * @param userRepository The {@link UserRepository} to be injected into a {@link UserManager} that will be tested.
     */
    public UserManagerTest(@Mock final UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userManager = new UserManager(userRepository);
    }


    /**
     * Tests that searching for a {@link User} that exists returns the expected {@link User}.
     */
    @Test
    void testSearchForUser() {
        final var username = generateAcceptedUsername();
        final var user = new User(username);
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        Assertions.assertTrue(userManager.getByUsername(username).isPresent(),
                "The manager is returning an empty optional when the user exists.");
        Assertions.assertEquals(username, userManager.getByUsername(username).map(User::getUsername).get(),
                "The returned user's username does not match the one used to search.");
    }

    /**
     * Tests that searching for a {@link User} that does not exist returns an empty {@link Optional}.
     */
    @Test
    void testSearchForNonExistenceUserReturnsEmptyOptional() {
        final var username = generateAcceptedUsername();
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Assertions.assertTrue(userManager.getByUsername(username).isEmpty(),
                "Searching for a user that does not exists is not returning an empty Optional.");
    }

    /**
     * Tests that searching for a {@link User} that does not exist does not throw any exception.
     */
    @Test
    void testSearchForNonExistenceUserDoesNotFail() {
        final var username = generateAcceptedUsername();
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Assertions.assertDoesNotThrow(() -> userManager.getByUsername(username),
                "Searching for a user that does not exists is throwing an exception.");
    }

    /**
     * Tests that creating a {@link User} with a username that is not used does not fail,
     * and that {@link UserRepository#save(Object)} is called at least once (which effectively saves the user).
     */
    @Test
    void testUserIsCreatedIfUsernameIsUnique() {
        final var username = generateAcceptedUsername();
        final var password = generateAcceptedPassword();
        Mockito.when(userRepository.existsByUsername(username)).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).then(invocation -> invocation.getArguments()[0]);
        Assertions.assertDoesNotThrow(() -> userManager.register(username, password),
                "Creating a user with a not used username fails.");
        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(Mockito.any(User.class));
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
        Mockito.when(userRepository.existsByUsername(username)).thenReturn(true);
        Assertions.assertThrows(UniqueViolationException.class, () -> userManager.register(username, password),
                "Creating a user with an already taken username does not fail.");
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
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
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).then(invocation -> invocation.getArguments()[0]);
        userManager.activate(username);
        Assertions.assertTrue(user.isActive(),
                "Activating a user through the manager is not changing the real user");
        userManager.getByUsername(username)
                .ifPresentOrElse(u -> Assertions.assertTrue(u.isActive(),
                        "Activating a user through the manager is not changing the user returned by it"),
                        () -> new IllegalStateException("Some test configuration is wrong."));
        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(user);
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
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).then(invocation -> invocation.getArguments()[0]);
        userManager.deactivate(username);
        Assertions.assertFalse(user.isActive(),
                "Activating a user through the manager is not changing the real user");
        userManager.getByUsername(username)
                .ifPresentOrElse(u -> Assertions.assertFalse(u.isActive(),
                        "Activating a user through the manager is not changing the user returned by it"),
                        () -> new IllegalStateException("Some test configuration is wrong."));
        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(user);
    }

    /**
     * Tests that activating a non existence {@link User} throws {@link NoSuchEntityException}.
     */
    @Test
    void testActivatingNonExistenceUserThrowsNoSuchEntityException() {
        testNonExistenceActionThrowsNoSuchEntityException(UserManager::activate,
                "Trying to activate a user that does not exist is not throwing NoSuchEntityException.");
    }

    /**
     * Tests that activating a non existence {@link User} throws {@link NoSuchEntityException}.
     */
    @Test
    void testDeactivatingNonExistenceUserThrowsNoSuchEntityException() {
        testNonExistenceActionThrowsNoSuchEntityException(UserManager::deactivate,
                "Trying to deactivate a user that does not exist is not throwing NoSuchEntityException.");
    }

    /**
     * Tests that deleting a {@link User} that exists does not fail, and that {@link UserRepository#delete(Object)}
     * is called at least once (which effectively deletes the user).
     */
    @Test
    void testDeletingAUserDoesNotFail() {
        final var username = generateAcceptedUsername();
        final var user = new User(username);
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        Assertions.assertDoesNotThrow(() -> userManager.delete(username), "Deleting a user is failing.");
        Mockito.verify(userRepository, Mockito.atLeastOnce()).delete(user);
    }

    /**
     * Tests that deleting a {@link User} that does not exist does not fail,
     * and that {@link UserRepository#delete(Object)} is not called at all.
     */
    @Test
    void testDeletingNonExistenceUserDoesNotFail() {
        final var username = generateAcceptedUsername();
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Assertions.assertDoesNotThrow(() -> userManager.delete(username), "Deleting a user is failing.");
        Mockito.verify(userRepository, Mockito.never()).delete(Mockito.any(User.class));
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
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Assertions.assertThrows(NoSuchEntityException.class,
                () -> userManagerAction.accept(userManager, username), message);
    }

    /**
     * @return A random username whose length is between the valid limit.
     */
    private static String generateAcceptedUsername() {
        return Faker.instance()
                .lorem()
                .fixedString(ValidationConstants.USERNAME_MAX_LENGTH)
                .replaceAll("\\s+", "a"); // Changes all white spaces into any character
    }

    /**
     * @return A random username whose length is between the valid limit.
     */
    private static String generateAcceptedPassword() {
        return "some password"; // TODO: improve when we have credentials.
    }
}
