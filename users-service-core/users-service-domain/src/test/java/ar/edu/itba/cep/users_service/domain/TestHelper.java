package ar.edu.itba.cep.users_service.domain;

import ar.edu.itba.cep.roles.Role;
import ar.edu.itba.cep.users_service.models.ValidationConstants;
import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.UUID;

/**
 * Class containing several helper methods for tests.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
/* package */ final class TestHelper {


    // ================================================================================================================
    // Valid values
    // ================================================================================================================

    /**
     * @return A random username whose length is between the valid limits.
     */
    /* package */
    static String validUsername() {
        return Faker.instance()
                .lorem()
                .fixedString(ValidationConstants.USERNAME_MAX_LENGTH)
                .replaceAll("\\s+", "a"); // Changes all white spaces into any character
    }

    /**
     * @return A password that is valid.
     */
    /* package */
    static String validPassword() {
        return "Some Password 1!";
    }

    /**
     * @return A random {@link Role}.
     */
    /* package */
    static Role randomRole() {
        final var roles = Role.values();
        final var index = (int) Faker.instance().number().numberBetween(0L, roles.length);
        return roles[index];
    }

    /**
     * @return A random token id.
     */
    /* package */
    static UUID validTokenId() {
        return UUID.randomUUID();
    }
}
