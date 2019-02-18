package ar.edu.itba.cep.users_service.models;

import com.bellotapps.webapps_commons.errors.ConstraintViolationError;
import com.bellotapps.webapps_commons.exceptions.CustomConstraintViolationException;
import com.bellotapps.webapps_commons.validation.annotations.ValidateConstraintsAfter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Represents a user of this application.
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * The user's id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // TODO: use a random generator?
    @Column(name = "id", nullable = false, updatable = false)
    private final long id;

    /**
     * The username.
     */
    @NotNull(message = "Username is missing.",
            payload = ConstraintViolationError.ErrorCausePayload.MissingValue.class)
    @Size(min = ValidationConstants.USERNAME_MIN_LENGTH,
            message = "Username too short",
            payload = ConstraintViolationError.ErrorCausePayload.IllegalValue.class)
    @Size(max = ValidationConstants.USERNAME_MAX_LENGTH,
            message = "Username too long",
            payload = ConstraintViolationError.ErrorCausePayload.IllegalValue.class)
    @Column(name = "username", nullable = false, updatable = false)
    private final String username;

    /**
     * A flag indicating whether this user is active (i.e can operate on the application).
     */
    @Column(name = "active", nullable = false)
    private boolean active;

    /**
     * Default constructor for Hibernate.
     */
    /* package */ User() {
        // Initialize final fields with default values. They will be overridden by Hibernate on initialization.
        this.id = 0;
        this.username = null;
    }

    /**
     * Constructor.
     *
     * @param username The username.
     * @throws CustomConstraintViolationException In case any value is not a valid one.
     */
    @ValidateConstraintsAfter
    public User(final String username) throws CustomConstraintViolationException {
        this.id = 0;
        this.username = username;
        this.active = true;
    }

    /**
     * @return The user's id.
     */
    public long getId() {
        return id;
    }

    /**
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return Indicates whether this user is active (i.e can operate on the application).
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Activates this user.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Deactivates this user.
     */
    public void deactivate() {
        this.active = false;
    }

    // ================================
    // equals, hashcode and toString
    // ================================

    /**
     * Equals based on the {@code id}.
     *
     * @param o The object to be compared with.
     * @return {@code true} if they are the equals, or {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        final var user = (User) o;

        return id == user.id;
    }

    /**
     * @return This user's hashcode, based on the {@code id}.
     */
    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "User: [" +
                "ID: " + id + ", " +
                "Username: " + username +
                ']';
    }
}
