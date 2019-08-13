package ar.edu.itba.cep.users_service.models;

import ar.edu.itba.cep.roles.Role;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.*;

/**
 * Represents a authentication/authorization token belonging to a {@link User}.
 */
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(of = "id", doNotUseGetters = true)
public class AuthToken {

    /**
     * The token's id.
     */
    private final UUID id;
    /**
     * The {@link User} owning the token.
     */
    private final User user;
    /**
     * The {@link Role}s assigned to the token
     * (i.e what the {@link User} is allowed to do when presenting this token).
     */
    private final Set<Role> rolesAssigned;
    /**
     * The {@link Instant} in which this token is created.
     */
    private final Instant createdAt;
    /**
     * A flag indicating whether this token is valid.
     */
    private boolean valid;


    /**
     * Default constructor.
     */
    /* package */ AuthToken() {
        // Initialize final fields with default values.
        this.id = null;
        this.user = null;
        this.rolesAssigned = null;
        this.createdAt = null;
    }

    /**
     * Constructor that assigned all the {@link User}'s {@link Role}s.
     *
     * @param user The {@link User} owning the token.
     */
    public AuthToken(final User user) {
        this(user, user.getRoles());
    }

    /**
     * Constructor that can assign a given {@link Set} of {@link Role}s.
     *
     * @param user          The {@link User} owning the token.
     * @param rolesAssigned The {@link Role}s assigned to the token
     *                      (i.e what the {@link User} is allowed to do when presenting this token).
     *                      Note that these {@link Role}s don't have to be the same
     *                      as the given {@code user} {@link Role}s
     *                      (for example, the {@code user} might have been granted a {@link Role} using only this
     *                      token).
     * @throws IllegalArgumentException In case any value is not a valid one.
     */
    public AuthToken(final User user, final Set<Role> rolesAssigned) throws IllegalArgumentException {
        assertUser(user);
        assertRoles(rolesAssigned);
        this.id = null;
        this.user = user;
        this.rolesAssigned = new HashSet<>(rolesAssigned);
        this.createdAt = Instant.now();
        this.valid = true;
    }


    /**
     * @return The {@link Role}s assigned to the token
     * (i.e what the {@link User} is allowed to do when presenting this token).
     */
    public Set<Role> getRolesAssigned() {
        return Collections.unmodifiableSet(rolesAssigned);
    }

    /**
     * Makes this token invalid.
     */
    public void invalidate() {
        this.valid = false;
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
        Assert.notNull(user, "The user must not be null");

    }

    /**
     * Asserts that the given {@code roles} {@link Set} is valid.
     *
     * @param roles The {@link Role}s {@link Set} to be checked.
     * @throws IllegalArgumentException In case the {@link Role}s {@link Set} is not a valid one.
     */
    private static void assertRoles(final Set<Role> roles) throws IllegalArgumentException {
        Assert.notNull(roles, "The roles Set must not be null");
        Assert.isTrue(roles.stream().noneMatch(Objects::isNull), "The roles set contains nulls");
    }
}
