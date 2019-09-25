package ar.edu.itba.cep.users_service.models;

import ar.edu.itba.cep.roles.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Test class for {@link AuthToken}.
 */
@ExtendWith(MockitoExtension.class)
class AuthTokenTest {


    // ================================================================================================================
    // Acceptable arguments
    // ================================================================================================================

    /**
     * Tests the creation of an AuthToken instance.
     *
     * @param user A {@link User} mock to be passed to the {@link AuthToken} creator method.
     */
    @Test
    void testCreation(@Mock(name = "user") final User user) {
        Assertions.assertAll(
                "Creating an AuthToken is not working as expected",
                () -> Assertions.assertDoesNotThrow(
                        () -> AuthToken.forUser(user),
                        "Fails when using just a user"
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> AuthToken.forUserWithRoles(user, null),
                        "Fails when using a null roles assigned set"
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> AuthToken.forUserWithRoles(user, new HashSet<>()),
                        "Fails when using an empty roles assigned set"
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> AuthToken.forUserWithRoles(user, new HashSet<>(Arrays.asList(Role.values()))),
                        "Fails when setting a user and a roles assigned set"
                )
        );
    }

    /**
     * Test that an {@link AuthToken} is valid when created.
     *
     * @param user A {@link User} mock to be passed to the {@link AuthToken} creator method.
     */
    @Test
    void testTokenIsValidUponCreation(@Mock(name = "user") final User user) {
        Assertions.assertTrue(AuthToken.forUser(user).isValid(), "The token is not valid upon creation");
    }


    // ================================================================================================================
    // Behaviour testing
    // ================================================================================================================

    /**
     * Test that an {@link AuthToken} is invalidated.
     *
     * @param user A {@link User} mock to be passed to the {@link AuthToken} creator method.
     */
    @Test
    void testInvalidation(@Mock(name = "user") final User user) {
        final var token = AuthToken.forUser(user);
        token.invalidate();
        Assertions.assertFalse(token.isValid(), "The invalidate operation did not have any effect");
    }


    // ================================================================================================================
    // Constraint testing
    // ================================================================================================================

    /**
     * Tests that passing a {@code null} {@link User} to any creator method throws an {@link IllegalArgumentException}.
     */
    @Test
    void testNullUser() {
        Assertions.assertAll(
                "Creating an AuthToken with a null user is being allowed",
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> AuthToken.forUser(null),
                        "When creating just with user"
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> AuthToken.forUserWithRoles(null, new HashSet<>(Arrays.asList(Role.values()))),
                        "When creating with user and roles"
                )
        );
    }

    /**
     * Tests that passing a {@link Set} containing a {@code null} element as the roles assigned set
     * throws an {@link IllegalArgumentException}.
     *
     * @param user A {@link User} mock to be passed to the {@link AuthToken} creator method.
     */
    @Test
    void testRolesAssignedSetWithNullElements(@Mock(name = "user") final User user) {
        final Set<Role> setWithNulls = new HashSet<>();
        setWithNulls.add(null);
        setWithNulls.addAll(Arrays.asList(Role.values()));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> AuthToken.forUserWithRoles(user, setWithNulls),
                "Setting a roles assigned set that contains a null element is being allowed"
        );
    }
}
