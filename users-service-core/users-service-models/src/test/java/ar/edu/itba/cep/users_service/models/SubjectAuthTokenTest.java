package ar.edu.itba.cep.users_service.models;

import ar.edu.itba.cep.roles.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Test class for {@link SubjectAuthToken}.
 */
@ExtendWith(MockitoExtension.class)
class SubjectAuthTokenTest extends AuthTokenTest<SubjectAuthToken> {

    /**
     * The subject used to create {@link SubjectAuthToken}s.
     */
    private final String subject = subject();

    // ================================================================================================================
    // Constraint testing
    // ================================================================================================================

    /**
     * Tests that passing a {@code null} {@code subject} to the constructor throws an {@link IllegalArgumentException}.
     */
    @Test
    void testNullSubject() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new SubjectAuthToken(null, roles()),
                "Creating a SubjectAuthToken with a null subject is being allowed"
        );
    }


    // ================================================================================================================
    // Abstract methods implementations
    // ================================================================================================================


    @Override
    String owner() {
        return subject;
    }

    @Override
    SubjectAuthToken buildToken() {
        return new SubjectAuthToken(subject, roles());
    }

    @Override
    SubjectAuthToken buildToken(final Set<Role> roles) {
        return new SubjectAuthToken(subject, roles);
    }

    @Override
    List<BuilderAndMessage<SubjectAuthToken>> builders() {
        return List.of(
                BuilderAndMessage.create(
                        () -> new SubjectAuthToken(subject, roles()),
                        "Fails when setting a subject and a roles set"),
                BuilderAndMessage.create(
                        () -> new SubjectAuthToken(subject, null),
                        "Fails when using a null roles assigned set"
                ),
                BuilderAndMessage.create(
                        () -> new SubjectAuthToken(subject, new HashSet<>()),
                        "Fails when using an empty roles assigned set"
                )
        );
    }

    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * @return A subject.
     */
    private static String subject() {
        return UUID.randomUUID().toString();
    }

    /**
     * @return A {@link Role}s {@link Set}.
     */
    private static Set<Role> roles() {
        return Arrays.stream(Role.values()).collect(Collectors.toSet());
    }
}
