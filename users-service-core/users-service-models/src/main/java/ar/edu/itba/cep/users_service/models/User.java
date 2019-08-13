package ar.edu.itba.cep.users_service.models;

import ar.edu.itba.cep.roles.Role;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static ar.edu.itba.cep.users_service.models.ValidationConstants.USERNAME_MAX_LENGTH;
import static ar.edu.itba.cep.users_service.models.ValidationConstants.USERNAME_MIN_LENGTH;

/**
 * Represents a user of this application.
 */
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(of = "id", doNotUseGetters = true)
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
     * Set containing the {@link Role}s this user has.
     */
    private final Set<Role> roles;

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
        this.roles = new HashSet<>();
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
        this.roles = new HashSet<>();
    }


    /**
     * @return The {@link Role}s this user has.
     */
    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }


    /**
     * Adds the given {@code role} to this user.
     *
     * @param role The {@link Role} to be added.
     */
    public void addRole(final Role role) {
        assertRole(role);
        this.roles.add(role);
    }

    /**
     * Removes the given {@code role} from this user.
     *
     * @param role The {@link Role} to be removed.
     */
    public void removeRole(final Role role) {
        this.roles.remove(role);
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

    /**
     * Asserts that the given {@code role} is valid.
     *
     * @param role The role to be checked.
     * @throws IllegalArgumentException In case the role is not a valid one.
     */
    private static void assertRole(final Role role) throws IllegalArgumentException {
        Assert.notNull(role, "The role is missing");
    }
}
