package ar.edu.itba.cep.users_service.models;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

/**
 * Test class for {@link UserCredential}.
 */
@ExtendWith(MockitoExtension.class)
class UserCredentialTest {

    /**
     * A mocked {@link User} that will own {@link UserCredential}s.
     */
    private final User mockedUser;

    /**
     * A mocked hashing {@link Function}.
     */
    private final Function<String, String> mockedHashingFunction;

    /**
     * Constructor.
     *
     * @param mockedUser            A mocked {@link User} that will own {@link UserCredential}s.
     * @param mockedHashingFunction A mocked hashing {@link Function}.
     */
    UserCredentialTest(@Mock final User mockedUser,
                       @Mock final Function<String, String> mockedHashingFunction) {
        this.mockedUser = mockedUser;
        this.mockedHashingFunction = mockedHashingFunction;
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown when trying to create a {@link UserCredential}
     * with a {@code null} {@link User}.
     */
    @Test
    void testNullUserThrowsIllegalArgumentException() {
        final var password = generateAcceptedPassword();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> UserCredential.buildCredential(null, password, mockedHashingFunction),
                "IllegalArgumentException is not thrown when creating a UserCredential with a null User.");
        Mockito.verifyZeroInteractions(mockedHashingFunction);
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown when trying to create a {@link UserCredential}
     * with a {@code null} hashing {@link Function}.
     */
    @Test
    void testNullHashingPasswordThrowsIllegalArgumentException() {
        final var password = generateAcceptedPassword();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> UserCredential.buildCredential(mockedUser, password, null),
                "IllegalArgumentException is not thrown when creating a UserCredential with a null hashing function.");
        Mockito.verifyZeroInteractions(mockedUser);
    }

    /**
     * Tests that using a null password does not create a {@link UserCredential}.
     */
    @Test
    void testNullPassword() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> UserCredential.buildCredential(mockedUser, null, mockedHashingFunction));
        Mockito.verifyZeroInteractions(mockedUser);
        Mockito.verifyZeroInteractions(mockedHashingFunction);
        testIllegalPassword(null,
                "Creating a credential with a null password must not be allowed.");
    }

    /**
     * Tests that using a too short password does not create a {@link UserCredential}.
     */
    @Test
    void testTooShortPassword() {
        testIllegalPassword(generateShortPassword(),
                "Creating a credential with a too short password must not be allowed.");
    }

    /**
     * Tests that using a too long password does not create a {@link UserCredential}.
     */
    @Test
    void testTooLongPassword() {
        testIllegalPassword(generateLongPassword(),
                "Creating a credential with a too long password must not be allowed.");
    }

    /**
     * Tests that using a password without a lowercase letter does not create a {@link UserCredential}.
     */
    @Test
    void testMissingLowerCasePassword() {
        testIllegalPassword(generateMissingLowerCasePassword(),
                "Creating a credential with a password without a lowercase letter must not be allowed.");
    }

    /**
     * Tests that using a password without an uppercase letter does not create a {@link UserCredential}.
     */
    @Test
    void testMissingUpperCasePassword() {
        testIllegalPassword(generateMissingUpperCasePassword(),
                "Creating a credential with a password without an uppercase letter must not be allowed.");
    }

    /**
     * Tests that using a password without a number does not create a {@link UserCredential}.
     */
    @Test
    void testMissingNumberPassword() {
        testIllegalPassword(generateMissingNumberPassword(),
                "Creating a credential with a password without a number must not be allowed.");
    }

    /**
     * Tests that using a password without a special character does not create a {@link UserCredential}.
     */
    @Test
    void testMissingSpecialCharacterPassword() {
        testIllegalPassword(generateMissingSpecialCharacterPassword(),
                "Creating a credential with a password without a special character must not be allowed.");
    }

    /**
     * Tests that using the given illegal password does not create a {@link UserCredential}
     * (i.e a {@link IllegalArgumentException} is thrown).
     * Also it verifies that there is no interaction with the {@code mockedUser} and the {@code mockedHashingFunction}.
     *
     * @param illegalPassword A password that is illegal.
     */
    void testIllegalPassword(final String illegalPassword, final String message) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> UserCredential.buildCredential(mockedUser, illegalPassword, mockedHashingFunction),
                message);
        Mockito.verifyZeroInteractions(mockedUser);
        Mockito.verifyZeroInteractions(mockedHashingFunction);
    }

    /**
     * @return A password that is valid.
     */
    private static String generateAcceptedPassword() {
        return "Some Password 1!";
    }

    /**
     * @return A password that is not valid only because its length is below the valid limit.
     */
    private static String generateShortPassword() {
        return "Asd!123";
    }

    /**
     * @return A password that is not valid only because its length is above the valid limit.
     */
    private static String generateLongPassword() {
        return "Ab1!" + Faker.instance()
                .lorem()
                .fixedString(ValidationConstants.PASSWORD_MAX_LENGTH)
                .replaceAll("\\s+", "a"); // Changes all white spaces into any character
    }

    /**
     * @return A password that is not valid because it does not contain any lowercase letter.
     */
    private static String generateMissingLowerCasePassword() {
        return "HELLO123-";
    }

    /**
     * @return A password that is not valid because it does not contain any uppercase letter.
     */
    private static String generateMissingUpperCasePassword() {
        return "hello123-";
    }

    /**
     * @return A password that is not valid because it does not contain any uppercase letter.
     */
    private static String generateMissingNumberPassword() {
        return "HelloWorld-";
    }

    /**
     * @return A password that is not valid because it does not contain any uppercase letter.
     */
    private static String generateMissingSpecialCharacterPassword() {
        return "HelloWorld1";
    }
}
