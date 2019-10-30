package ar.edu.itba.cep.users_service.models;

import ar.edu.itba.cep.roles.Role;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;

/**
 * Test class for {@link UserAuthToken}.
 */
@ExtendWith(MockitoExtension.class)
class UserAuthTokenTest extends AuthTokenTest<UserAuthToken> {

    /**
     * A mocked {@link User} used to be passed to factory methods.
     */
    private final User user;

    /**
     * Constructor
     *
     * @param user A mocked {@link User} used to be passed to factory methods.
     */
    UserAuthTokenTest(@Mock(name = "user") final User user) {
        this.user = user;
    }

    /**
     * Setups the username of the {@link #user} mock.
     */
    @BeforeEach
    void setupUsername() {
        when(user.getUsername()).thenReturn(username());
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
                        () -> UserAuthToken.forUser(null),
                        "When creating just with user"
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> UserAuthToken.forUserWithRoles(null, new HashSet<>(Arrays.asList(Role.values()))),
                        "When creating with user and roles"
                )
        );
    }


    // ================================================================================================================
    // Abstract methods implementations
    // ================================================================================================================


    @Override
    String owner() {
        return user.getUsername();
    }

    @Override
    UserAuthToken buildToken() {
        return UserAuthToken.forUser(user);
    }

    @Override
    UserAuthToken buildToken(final Set<Role> roles) {
        return UserAuthToken.forUserWithRoles(user, roles);
    }

    @Override
    List<BuilderAndMessage<UserAuthToken>> builders() {
        return List.of(
                BuilderAndMessage.create(
                        () -> UserAuthToken.forUser(user),
                        "Fails when using just a user"),
                BuilderAndMessage.create(
                        () -> UserAuthToken.forUserWithRoles(user, null),
                        "Fails when using a null roles assigned set"
                ),
                BuilderAndMessage.create(
                        () -> UserAuthToken.forUserWithRoles(user, new HashSet<>()),
                        "Fails when using an empty roles assigned set"
                ),
                BuilderAndMessage.create(
                        () -> UserAuthToken.forUserWithRoles(user, new HashSet<>(Arrays.asList(Role.values()))),
                        "Fails when setting a user and a roles assigned set"
                )
        );
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * @return A username.
     */
    private static String username() {
        return Faker.instance().name().username();
    }
}
