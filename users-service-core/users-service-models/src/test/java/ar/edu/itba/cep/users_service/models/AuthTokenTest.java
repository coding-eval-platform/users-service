package ar.edu.itba.cep.users_service.models;

import ar.edu.itba.cep.roles.Role;
import lombok.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Test class for {@link AuthToken}.
 *
 * @param <T> The concrete type of {@link AuthToken}.
 */
abstract class AuthTokenTest<T extends AuthToken> {

    // ================================================================================================================
    // Acceptable arguments
    // ================================================================================================================

    /**
     * Tests the creation methods returned by the {@link #builders()} method.
     */
    @Test
    void testCreation() {
        final var builders = builders().stream()
                .map(c ->
                        (Executable) () -> Assertions.assertDoesNotThrow(
                                () -> c.getBuilder().get(),
                                c.getMessage()
                        )
                )
                .toArray(Executable[]::new);

        Assertions.assertAll("Creating an AuthToken is not working as expected", builders);
    }

    /**
     * Test that an {@link AuthToken} is valid when created.
     */
    @Test
    void testTokenIsValidUponCreation() {
        Assertions.assertTrue(buildToken().isValid(), "The token is not valid upon creation");
    }

    /**
     * Tests that the returned owner of a created {@link AuthToken} is the expected.
     */
    @Test
    void testOwner() {
        Assertions.assertEquals(owner(), buildToken().getOwner(), "There is a mismatch in the owner");
    }


    // ================================================================================================================
    // Behaviour testing
    // ================================================================================================================

    /**
     * Test that an {@link AuthToken} is invalidated.
     */
    @Test
    void testInvalidation() {
        final var token = buildToken();
        token.invalidate();
        Assertions.assertFalse(token.isValid(), "The invalidate operation did not have any effect");
    }


    // ================================================================================================================
    // Constraint testing
    // ================================================================================================================

    /**
     * Tests that passing a {@link Set} containing a {@code null} element as the roles assigned set
     * throws an {@link IllegalArgumentException}.
     */
    @Test
    void testRolesAssignedSetWithNullElements() {
        final Set<Role> setWithNulls = new HashSet<>();
        setWithNulls.add(null);
        setWithNulls.addAll(Arrays.asList(Role.values()));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> buildToken(setWithNulls),
                "Setting a roles assigned set that contains a null element is being allowed"
        );
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    abstract String owner();

    /**
     * Builds an {@link AuthToken}. This method is used to test stuff once an {@link AuthToken} is build as,
     * for example, how invalidation works.
     *
     * @return The built {@link AuthToken}.
     */
    abstract T buildToken();

    /**
     * Builds an {@link AuthToken}, passing the {@link Role}s {@link Set}.
     * This method is used to test stuff on creation of {@link AuthToken}s
     * when the creation needs the {@link Role}s {@link Set}.
     *
     * @param roles The {@link Role}s {@link Set}.
     * @return The built {@link AuthToken}.
     */
    abstract T buildToken(final Set<Role> roles);

    /**
     * Returns a {@link List} of {@link BuilderAndMessage} instances.
     * This method is used to test creation methods (constructor and factory methods).
     *
     * @return The {@link List} of {@link BuilderAndMessage} instances.
     */
    abstract List<BuilderAndMessage<T>> builders();


    /**
     * Container class that holds a {@link Supplier} of {@link AuthToken} to be tested,
     * together with a message that will be displayed in case the assertion fails.
     *
     * @param <T> The concrete type of {@link AuthToken}.
     */
    @Value(staticConstructor = "create")
    /* package */ static final class BuilderAndMessage<T extends AuthToken> {
        private final Supplier<T> builder;
        private final String message;
    }
}
