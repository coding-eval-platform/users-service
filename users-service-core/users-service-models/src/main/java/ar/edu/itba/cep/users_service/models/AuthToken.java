package ar.edu.itba.cep.users_service.models;

import ar.edu.itba.cep.roles.Role;
import lombok.*;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.*;

/**
 * Represents a authentication/authorization token belonging to a {@link User}.
 */
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(of = "id", doNotUseGetters = true)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public abstract class AuthToken {

    /**
     * The token's id.
     */
    private final UUID id;
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
     * Constructor that can assign a given {@link Set} of {@link Role}s.
     *
     * @param rolesAssigned The {@link Role}s assigned to the token
     *                      (i.e what the {@link User} is allowed to do when presenting this token).
     *                      Note that these {@link Role}s don't have to be the same
     *                      as the given {@code user} {@link Role}s
     *                      (for example, the {@code user} might have been granted a {@link Role} using only this
     *                      token).
     * @throws IllegalArgumentException In case any value is not a valid one.
     */
    public AuthToken(final Set<Role> rolesAssigned) throws IllegalArgumentException {
        assertRoles(rolesAssigned);

        this.id = null;
        this.rolesAssigned = Optional.ofNullable(rolesAssigned).map(HashSet::new).orElseGet(HashSet::new);
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


    /**
     * @return A {@link String} representation of the owner of the token (e.g username, subject, etc).
     */
    public abstract String getOwner();


    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code roles} {@link Set} is valid.
     *
     * @param roles The {@link Role}s {@link Set} to be checked.
     * @throws IllegalArgumentException In case the {@link Role}s {@link Set} is not a valid one.
     */
    private static void assertRoles(final Set<Role> roles) throws IllegalArgumentException {
        Assert.isTrue(
                Objects.isNull(roles) || roles.stream().noneMatch(Objects::isNull),
                "If present, the roles set must not contain nulls"
        );
    }
}
