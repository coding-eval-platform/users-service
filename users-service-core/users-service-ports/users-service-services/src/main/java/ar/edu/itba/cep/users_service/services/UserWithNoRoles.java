package ar.edu.itba.cep.users_service.services;

import ar.edu.itba.cep.users_service.models.User;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A Data Transfer Object that wraps a {@link User}, without exposing the {@link User#getRoles()}
 * method.
 */
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true, of = "user")
public class UserWithNoRoles {

    /**
     * The {@link User} being wrapped.
     */
    private final User user;


    /**
     * Constructor.
     *
     * @param user The {@link User} to be wrapped.
     */
    public UserWithNoRoles(final User user) {
        this.user = user;
    }


    /**
     * @return The wrapped {@link User}'s username.
     */
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * @return Whether the wrapped {@link User} is active.
     */
    public boolean isActive() {
        return user.isActive();
    }
}
