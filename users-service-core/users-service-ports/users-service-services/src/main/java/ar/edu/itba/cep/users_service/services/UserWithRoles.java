package ar.edu.itba.cep.users_service.services;

import ar.edu.itba.cep.users_service.models.Role;
import ar.edu.itba.cep.users_service.models.User;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A Data Transfer Object that wraps a {@link User}, exposing the {@link User#getRoles()} method.
 */
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true, of = "user")
public class UserWithRoles {

    /**
     * The {@link User} being wrapped.
     */
    private final User user;


    /**
     * Constructor.
     *
     * @param user The {@link User} to be wrapped.
     */
    public UserWithRoles(final User user) {
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

    /**
     * @return The {@link User}'s {@link Role}s.
     */
    public Set<Role> getRoles() {
        return user.getRoles();
    }
}
