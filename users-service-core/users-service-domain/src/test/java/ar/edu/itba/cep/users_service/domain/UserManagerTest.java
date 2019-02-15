package ar.edu.itba.cep.users_service.domain;

import ar.edu.itba.cep.users_service.domain.test_config.DomainTestConfig;
import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.models.ValidationConstants;
import ar.edu.itba.cep.users_service.repositories.UserRepository;
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

    @Test
    void testUserIsCreatedIfUsernameIsUnique() {
        final var username = generateAcceptedUsername();
        final var password = generateAcceptedPassword();
        Mockito.when(userRepository.existsByUsername(username)).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).then(invocation -> invocation.getArguments()[0]);
        Assertions.assertDoesNotThrow(() -> userManager.register(username, password));
    }

    /**
     * Tests that username uniqueness is taken into account,
     * throwing a {@link UniqueViolationException} if the username is already in use.
     */
    @Test
    void testUsernameUniqueness() {
        final var username = generateAcceptedUsername();
        final var password = generateAcceptedPassword();
        Mockito.when(userRepository.existsByUsername(username)).thenReturn(true);
        Assertions.assertThrows(UniqueViolationException.class,
                () -> userManager.register(username, password));
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
