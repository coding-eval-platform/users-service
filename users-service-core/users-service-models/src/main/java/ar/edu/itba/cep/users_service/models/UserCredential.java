package ar.edu.itba.cep.users_service.models;

import com.bellotapps.webapps_commons.errors.ConstraintViolationError;
import com.bellotapps.webapps_commons.exceptions.CustomConstraintViolationException;
import com.bellotapps.webapps_commons.validation.annotations.ValidateConstraintsBefore;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.function.Function;

/**
 * Represents a credential for {@link User}s of this application.
 */
@Entity
@Table(name = "user_credentials")
public class UserCredential {

    /**
     * The credential id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private final long id;

    /**
     * The {@link User} owning this credential.
     */
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private final User user;

    /**
     * The hashed password.
     */
    @Column(name = "hashed_password", nullable = false, updatable = false)
    private final String hashedPassword;

    /**
     * {@link Instant} at which this credential is created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
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
     * @throws CustomConstraintViolationException If any of the values is not valid.
     */
    public static UserCredential buildCredential(final User user,
                                                 final String password,
                                                 final Function<String, String> hashingFunction)
            throws CustomConstraintViolationException {
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
        @NotNull(message = "Password is missing.",
                payload = ConstraintViolationError.ErrorCausePayload.MissingValue.class)
        @Size(min = ValidationConstants.PASSWORD_MIN_LENGTH,
                message = "Password too short",
                payload = ConstraintViolationError.ErrorCausePayload.IllegalValue.class)
        @Size(max = ValidationConstants.PASSWORD_MAX_LENGTH,
                message = "Password too long",
                payload = ConstraintViolationError.ErrorCausePayload.IllegalValue.class)
        @Pattern(regexp = ".*[a-z].*",
                message = "Password must contain a lowercase letter",
                payload = ConstraintViolationError.ErrorCausePayload.IllegalValue.class)
        @Pattern(regexp = ".*[A-Z].*",
                message = "Password must contain am uppercase letter",
                payload = ConstraintViolationError.ErrorCausePayload.IllegalValue.class)
        @Pattern(regexp = ".*\\d.*",
                message = "Password must contain a number",
                payload = ConstraintViolationError.ErrorCausePayload.IllegalValue.class)
        @Pattern(regexp = "^.*[^a-zA-Z0-9].*$",
                message = "Password must contain a special character",
                payload = ConstraintViolationError.ErrorCausePayload.IllegalValue.class)
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
         * @throws CustomConstraintViolationException If any value violates the given constraints.
         */
        private UserCredential build() throws CustomConstraintViolationException {
            Assert.notNull(user, "The user must be set.");
            Assert.notNull(hashingFunction, "The hashing function must be set.");
            return doBuild();
        }

        /**
         * Method that actually performs the creation of the credential.
         *
         * @return The created credential.
         * @throws CustomConstraintViolationException If any value violates the given constraints.
         * @implNote This method expects that not-null validation of {@code user} and {@code hashingFunction}
         * is already performed.
         */
        @ValidateConstraintsBefore
        private UserCredential doBuild() throws CustomConstraintViolationException {
            return new UserCredential(user, hashingFunction.apply(password));
        }
    }
}
