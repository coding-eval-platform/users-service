package ar.edu.itba.cep.users_service.models;

import ar.edu.itba.cep.users_service.models.config.ModelsConfig;
import com.bellotapps.webapps_commons.exceptions.CustomConstraintViolationException;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

/**
 * Test class for the user model.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ModelsConfig.class
})
class UserTest {

    /**
     * Tests that a {@link User} is created with an acceptable username length.
     */
    @Test
    void testAcceptableUsername() {
        final var username = generateAcceptedUsername();
        new User(username);
    }

    /**
     * Tests that a {@link User} cannot be created if the username is too long.
     */
    @Test
    void testLongUsername() {
        final var longUsername = generateLongUsername();
        Assertions.assertThrows(CustomConstraintViolationException.class, () -> new User(longUsername));
    }

    /**
     * Tests that a {@link User} cannot be created if the username is too short.
     */
    @Test
    void testShortUsername() {
        final var shortUsernameOptional = generateShortUsername();
        // If the username is present, then perform test. Otherwise, it means that there is no minimum length.
        shortUsernameOptional.ifPresent(shortUsername ->
                Assertions.assertThrows(CustomConstraintViolationException.class, () -> new User(shortUsername)));
    }

    /**
     * Tests that a {@link User} is active when created.
     */
    @Test
    void testUserIsActiveWhenCreated() {
        final var user = new User(generateAcceptedUsername());
        Assertions.assertTrue(user.isActive(), "The user is not active when it was just created.");
    }

    /**
     * Tests that a {@link User} is not active when it was deactivated.
     */
    @Test
    void testDeactivateUser() {
        final var user = new User(generateAcceptedUsername());
        user.deactivate();
        Assertions.assertFalse(user.isActive(), "The user is active after deactivating it.");
    }

    /**
     * Tests that a {@link User} that was deactivated and then activated again is active.
     */
    @Test
    void testReactivatedUser() {
        final var user = new User(generateAcceptedUsername());
        user.deactivate();
        user.activate();
        Assertions.assertTrue(user.isActive(), "The user is not active after reactivating it.");
    }


    /**
     * @return A random username whose length is between the valid limit.
     */
    private static String generateAcceptedUsername() {
        return generateUsernameOfLength(ValidationConstants.USERNAME_MAX_LENGTH);
    }

    /**
     * @return A random username whose length is above the valid limit.
     */
    private static String generateLongUsername() {
        return generateUsernameOfLength(ValidationConstants.USERNAME_MAX_LENGTH + 1);
    }

    /**
     * @return An {@link Optional} containing a username whose length is below the valid limit
     * if there is such limit (i.e th min length is positive). Otherwise, an empty {@link Optional} is returned.
     */
    private static Optional<String> generateShortUsername() {
        if (ValidationConstants.USERNAME_MIN_LENGTH > 0) {
            return Optional.of(generateUsernameOfLength(ValidationConstants.USERNAME_MIN_LENGTH - 1));
        }
        return Optional.empty();
    }

    /**
     * Creates a username whose length is the given one.
     *
     * @param length The length of the username.
     * @return The generated username.
     */
    private static String generateUsernameOfLength(final int length) {
        return Faker.instance()
                .lorem()
                .fixedString(length)
                .replaceAll("\\s+", "a"); // Changes all white spaces into any character
    }
}
