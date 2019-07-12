package ar.edu.itba.cep.users_service.models;

import org.springframework.util.Assert;

import static ar.edu.itba.cep.users_service.models.ValidationConstants.USERNAME_MAX_LENGTH;
import static ar.edu.itba.cep.users_service.models.ValidationConstants.USERNAME_MIN_LENGTH;

/**
 * Represents a user of this application.
 */
public class User {

    /**
     * The user's id.
     */
    private final long id;

    /**
     * The username.
     */
    private final String username;

    /**
     * A flag indicating whether this user is active (i.e can operate on the application).
     */
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
     * @throws IllegalArgumentException In case any value is not a valid one.
     */
    public User(final String username) throws IllegalArgumentException {
        assertUsername(username);

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

    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code username} is valid.
     *
     * @param username The username to be checked.
     * @throws IllegalArgumentException In case the username is not a valid one.
     */
    private static void assertUsername(final String username) throws IllegalArgumentException {
        Assert.notNull(username, "The username is missing");
        Assert.isTrue(username.length() >= USERNAME_MIN_LENGTH, "The username is too short");
        Assert.isTrue(username.length() <= USERNAME_MAX_LENGTH, "The username is too long");
    }
}
