package ar.edu.itba.cep.users_service.models;

import ar.edu.itba.cep.roles.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * Represents a authentication/authorization token that can be issued to {@link User}s of the platform.
 */
@Getter
@ToString(doNotUseGetters = true, callSuper = true)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class UserAuthToken extends AuthToken {

    /**
     * The {@link User} owning the token.
     */
    private final User user;


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
    public UserAuthToken(final User user, final Set<Role> rolesAssigned) throws IllegalArgumentException {
        super(rolesAssigned);
        assertUser(user);

        this.user = user;
    }


    @Override
    public String getOwner() {
        return user.getUsername();
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

    // ================================
    // Factory methods
    // ================================

    /**
     * Creates a {@link UserAuthToken} for the given {@code user}.
     *
     * @param user The {@link User} for which the {@link UserAuthToken} is being created.
     * @return The created {@link UserAuthToken}.
     */
    public static UserAuthToken forUser(final User user) {
        return new UserAuthToken(user, user.getRoles());
    }

    /**
     * Creates an {@link UserAuthToken} for the given {@code user}, assigning the given {@code roles}.
     *
     * @param user  The {@link User} for which the {@link UserAuthToken} is being created.
     * @param roles The {@link Role}s assigned to the token
     *              (i.e what the {@link User} is allowed to do when presenting this token).
     *              Note that these {@link Role}s don't have to be the same
     *              as the given {@code user} {@link Role}s
     *              (for example, the {@code user} might have been granted a {@link Role} using only this
     *              token).
     * @return The created {@link UserAuthToken}.
     */
    public static UserAuthToken forUserWithRoles(final User user, final Set<Role> roles) {
        return new UserAuthToken(user, roles);
    }
}
