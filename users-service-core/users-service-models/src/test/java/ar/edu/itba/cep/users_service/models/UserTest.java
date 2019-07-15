package ar.edu.itba.cep.users_service.models;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

/**
 * Test class for the user model.
 */
class UserTest {

    /**
     * Tests that a {@link User} is created with an acceptable username length.
     */
    @Test
    void testAcceptableUsername() {
        final var username = generateAcceptedUsername();
        Assertions.assertDoesNotThrow(() -> new User(username),
                "The user is not being created with an acceptable username.");
    }

    @Test
    void testNullUsername() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new User(null),
                "Creating a user with a null username must not be allowed.");
    }

    /**
     * Tests that a {@link User} cannot be created if the username is too long.
     */
    @Test
    void testLongUsername() {
        final var longUsername = generateLongUsername();
        Assertions.assertThrows(IllegalArgumentException.class, () -> new User(longUsername),
                "Creating a user with a too long username must not be allowed.");
    }

    /**
     * Tests that a {@link User} cannot be created if the username is too short.
     */
    @Test
    void testShortUsername() {
        final var shortUsernameOptional = generateShortUsername();
        // If the username is present, then perform test. Otherwise, it means that there is no minimum length.
        shortUsernameOptional.ifPresent(shortUsername ->
                Assertions.assertThrows(IllegalArgumentException.class, () -> new User(shortUsername),
                        "Creating a user with a too short username must not be allowed."));
    }

    /**
     * Tests that a {@link User} is active when created.
     */
    @Test
    void testUserIsActiveWhenCreated() {
        final var user = getUser();
        Assertions.assertTrue(user.isActive(), "The user is not active when it was just created.");
    }

    /**
     * Tests that a {@link User} is not active when it was deactivated.
     */
    @Test
    void testDeactivateUser() {
        final var user = getUser();
        user.deactivate();
        Assertions.assertFalse(user.isActive(), "The user is active after deactivating it.");
    }

    /**
     * Tests that a {@link User} that was deactivated and then activated again is active.
     */
    @Test
    void testReactivatedUser() {
        final var user = getUser();
        user.deactivate();
        user.activate();
        Assertions.assertTrue(user.isActive(), "The user is not active after reactivating it.");
    }

    /**
     * Tests that a {@link User} is created with any {@link Role} assigned.
     */
    @Test
    void testUserHasNoRolesWhenCreated() {
        Assertions.assertTrue(
                getUser().getRoles().isEmpty(),
                "A user must not have any role assigned when created"
        );
    }

    /**
     * Tests that the returned {@link java.util.Set} of {@link Role}s cannot be modified from the outside.
     * This test checks that any exception is thrown if any modification is tried to be performed over the
     * returned {@link java.util.Set}.
     */
    @Test
    void testRolesSetIsUnmodifiable() {
        Assertions.assertAll(
                "The returned set of roles by a User is not unmodifiable",
                () -> Assertions.assertThrows(
                        Throwable.class,
                        () -> getUser().getRoles().add(getARole()),
                        "A role can be added"
                ),
                () -> Assertions.assertThrows(
                        Throwable.class,
                        () -> getUser().getRoles().remove(getARole()),
                        "A role can be removed"
                ),
                () -> Assertions.assertThrows(
                        Throwable.class,
                        () -> getUser().getRoles().addAll(List.of(getARole(), getARole())),
                        "Roles can be added using the addAll method"
                ),
                () -> Assertions.assertThrows(
                        Throwable.class,
                        () -> getUser().getRoles().removeAll(List.of(getARole(), getARole())),
                        "Roles can be removed using the removeAll method"
                ),
                () -> Assertions.assertThrows(
                        Throwable.class,
                        () -> getUser().getRoles().retainAll(List.of(getARole(), getARole())),
                        "The roles Set can be modified using the retainAll method"
                ),
                () -> Assertions.assertThrows(
                        Throwable.class,
                        () -> getUser().getRoles().clear(),
                        "The roles Set can be cleared"
                ),
                () -> Assertions.assertThrows(
                        Throwable.class,
                        () -> getUser().getRoles().removeIf(ignored -> true),
                        "Roles can be modified using removeIf"
                )
        );
    }

    /**
     * Tests that adding a {@link Role} works as expected.
     */
    @Test
    void testAddRole() {
        final var user = getUser();
        final var role = getARole();
        user.addRole(role);
        Assertions.assertTrue(
                () -> user.getRoles().contains(role),
                "A role is not contained in the User's Role Set after being added."
        );
    }

    /**
     * Tests that adding a {@link Role} is idempotent
     * (adds the same {@link Role} twice, and checks that the amount of {@link Role}s is the same).
     */
    @Test
    void testAddRoleTwice() {
        final var user = getUser();
        final var role = getARole();
        user.addRole(role);
        final var amountOfRoles = user.getRoles().size();
        user.addRole(role);
        Assertions.assertEquals(
                amountOfRoles,
                user.getRoles().size(),
                "Adding an already contained role increments the returned Role's Set size."
        );
    }

    /**
     * Tests that removing a {@link Role} works as expected.
     */
    @Test
    void testRemoveContainedRole() {
        final var user = getUser();
        final var role = getARole();
        user.addRole(role);
        user.removeRole(role);
        Assertions.assertFalse(
                () -> user.getRoles().contains(role),
                "A role is contained in the User's Role Set after being removed."
        );
    }

    /**
     * Tests that removing a {@link Role} that is not assigned to a {@link User} does not throw any exception.
     */
    @Test
    void testRemoveNotContainedRole() {
        final var user = getUser();
        final var role = getARole();
        user.removeRole(role);
        Assertions.assertDoesNotThrow(
                () -> user.addRole(role),
                "An unexpected exception is thrown when removing a not contained role."
        );
    }


    /**
     * Tests that adding a {@code null} {@link Role} throws an {@link IllegalArgumentException}.
     */
    @Test
    void testAddNullRole() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> getUser().addRole(null),
                "Adding a null Role is being allowed."
        );
    }

    /**
     * Tests that removing a {@code null} {@link Role} does not throw any exception.
     */
    @Test
    void testRemoveRole() {
        Assertions.assertAll(
                "Removing a null Role does not work as expected.",
                () -> Assertions.assertDoesNotThrow(
                        () -> getUser().removeRole(null),
                        "Throws an exception if the roles Set is empty."
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> {
                            final var user = getUser();
                            user.addRole(getARole());
                            user.removeRole(null);
                        },
                        "Throws an exception if the roles Set is not empty."
                ),
                () -> {
                    final var user = getUser();
                    user.addRole(getARole());
                    final var rolesAmount = user.getRoles().size();
                    user.removeRole(null);
                    Assertions.assertEquals(
                            rolesAmount,
                            user.getRoles().size(),
                            "The size of the roles Set changed after trying to remove a null Role."
                    );
                }
        );

    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Creates a {@link User} with valid arguments.
     *
     * @return A {@link User}.
     */
    private static User getUser() {
        return new User(
                generateAcceptedUsername()
        );
    }


    // ================================
    // Valid Arguments
    // ================================

    /**
     * @return A random username whose length is between the valid limits.
     */
    private static String generateAcceptedUsername() {
        return generateUsernameOfLength(ValidationConstants.USERNAME_MAX_LENGTH);
    }

    /**
     * @return A random {@link Role}.
     */
    private static Role getARole() {
        final var roles = Role.values();
        final var index = (int) Faker.instance().number().numberBetween(0L, roles.length);
        return roles[index];
    }


    // ================================
    // Invalid Arguments
    // ================================

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
