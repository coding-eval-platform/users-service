package ar.edu.itba.cep.users_service.models;

import org.springframework.util.Assert;

import java.time.Instant;
import java.util.function.Function;

import static ar.edu.itba.cep.users_service.models.ValidationConstants.*;

/**
 * Represents a credential for {@link User}s of this application.
 */
public class UserCredential {

    /**
     * The credential id.
     */
    private final long id;

    /**
     * The {@link User} owning this credential.
     */
    private final User user;

    /**
     * The hashed password.
     */
    private final String hashedPassword;

    /**
     * {@link Instant} at which this credential is created.
     */
    private final Instant createdAt;


    /**
     * Default constructor for Hibernate
     */
    /* package */ UserCredential() {
        // Initialize final fields with default values. They will be overridden by Hibernate on initialization.
        this.id = 0;
        this.user = null;
        this.hashedPassword = null;
        this.createdAt = null;
    }

    /**
     * Private constructor.
     * To create a credential instance, use {@link #buildCredential(User, String, Function)}.
     *
     * @param user           The {@link User} owning this credential.
     * @param hashedPassword The hashed password.
     */
    private UserCredential(final User user, final String hashedPassword) {
        this.id = 0;
        this.user = user;
        this.hashedPassword = hashedPassword;
        this.createdAt = Instant.now();
    }

    /**
     * @return The credential id.
     */
    public long getId() {
        return id;
    }

    /**
     * @return The {@link User} owning this credential.
     */
    public User getUser() {
        return user;
    }

    /**
     * @return The hashed password.
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * @return {@link Instant} at which this credential is created.
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Creates a new {@link UserCredential}.
     *
     * @param user            The {@link User} that will own the new credential.
     * @param password        The plain password.
     * @param hashingFunction A {@link Function} that takes the password (which is plain), and hashes it.
     * @return A new {@link UserCredential} instance.
     * @throws IllegalArgumentException If any of the values is not valid.
     */
    public static UserCredential buildCredential(
            final User user,
            final String password,
            final Function<String, String> hashingFunction) throws IllegalArgumentException {
        return new CredentialsBuilder()
                .forUser(user)
                .withPassword(password)
                .withHashingFunction(hashingFunction)
                .build();
    }


    // ================================
    // Builder
    // ================================

    /**
     * Helper class used to create {@link UserCredential}s.
     * This class declares all the constraints,
     * which are validated before the {@link CredentialsBuilder#build()} method is executed.
     */
    private static final class CredentialsBuilder {
        /**
         * The {@link User} that will own the built credential.
         */
        private User user;

        /**
         * The plain password.
         */
        private String password;

        /**
         * A {@link Function} that takes the password (which is plain), and encodes it
         * (performs a hashing operation over it).
         */
        private Function<String, String> hashingFunction;

        /**
         * Sets the {@link User} that will own the built credential.
         *
         * @param user The {@link User} that will own the built credential.
         * @return {@code this}, for method chaining.
         */
        private CredentialsBuilder forUser(final User user) {
            this.user = user;
            return this;
        }

        /**
         * Sets the plain password.
         *
         * @param password The plain password.
         * @return {@code this}, for method chaining.
         */
        private CredentialsBuilder withPassword(final String password) {
            this.password = password;
            return this;
        }

        /**
         * Sets the function to be used to hash the plain password.
         *
         * @param hashingFunction A {@link Function} that takes the password (which is plain), and encodes it
         *                        (performs a hashing operation over it).
         * @return {@code this}, for method chaining.
         */
        private CredentialsBuilder withHashingFunction(final Function<String, String> hashingFunction) {
            this.hashingFunction = hashingFunction;
            return this;
        }

        /**
         * Builds the {@link UserCredential} using the set values.
         *
         * @return The created credential.
         * @throws IllegalArgumentException If any value violates the constraints.
         */
        private UserCredential build() throws IllegalArgumentException {
            assertUser(user);
            assertHashingFunction(hashingFunction);
            assertPassword(password);

            return new UserCredential(user, hashingFunction.apply(password));
        }


        // ================================
        // Assertions
        // ================================

        /**
         * Asserts that the given {@code user} is valid.
         *
         * @param user The {@link User} to be checked.
         * @throws IllegalArgumentException In case the user is not a valid one.
         */
        private static void assertUser(final User user) throws IllegalArgumentException {
            Assert.notNull(user, "The user is missing");
        }

        /**
         * Asserts that the given {@code password} is valid.
         *
         * @param password The password to be checked.
         * @throws IllegalArgumentException In case the password is not a valid one.
         */
        private static void assertPassword(final String password) throws IllegalArgumentException {
            Assert.notNull(password, "The password is missing");
            Assert.isTrue(password.length() >= PASSWORD_MIN_LENGTH, "Password too short");
            Assert.isTrue(password.length() <= PASSWORD_MAX_LENGTH, "Password too long");
            Assert.isTrue(
                    password.matches(PASSWORD_CONTAINS_LOWERCASE_REGEX),
                    "Password must contain a lowercase letter"
            );
            Assert.isTrue(
                    password.matches(PASSWORD_CONTAINS_UPPERCASE_REGEX),
                    "Password must contain a lowercase letter"
            );
            Assert.isTrue(
                    password.matches(PASSWORD_CONTAINS_NUMBER_REGEX),
                    "Password must contain a number"
            );
            Assert.isTrue(
                    password.matches(PASSWORD_CONTAINS_SPECIAL_CHARACTER_REGEX),
                    "Password must contain a special character"
            );
        }

        /**
         * Asserts that the given {@code hashingFunction} is valid.
         *
         * @param hashingFunction The hashing {@link Function} to be checked.
         * @throws IllegalArgumentException In case the hashing function is not a valid one.
         */
        private static void assertHashingFunction(final Function<String, String> hashingFunction)
                throws IllegalArgumentException {
            Assert.notNull(hashingFunction, "The hashing function is missing");
        }
    }
}
