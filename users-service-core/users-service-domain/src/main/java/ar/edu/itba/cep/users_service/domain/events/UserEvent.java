package ar.edu.itba.cep.users_service.domain.events;

import ar.edu.itba.cep.users_service.models.Role;
import ar.edu.itba.cep.users_service.models.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

/**
 * Base class for events affecting a {@link User}.
 */
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
public abstract class UserEvent {

    /**
     * The user being affected.
     */
    private final User user;


    /**
     * Constructor.
     *
     * @param user The {@link User} being affected.
     */
    /* package */ UserEvent(final User user) {
        Assert.notNull(user, "The user must not be null");
        this.user = user;
    }


    /**
     * Creates a {@link UserRoleRemovedEvent}.
     *
     * @param user The {@link User} being affected.
     * @param role The {@link Role} being removed.
     * @return The created {@link UserRoleRemovedEvent}.
     */
    public static UserRoleRemovedEvent roleRemoved(final User user, final Role role) {
        return new UserRoleRemovedEvent(user, role);
    }

    /**
     * Creates a {@link UserDeactivatedEvent}.
     *
     * @param user The {@link User} being deactivated.
     * @return The created {@link UserDeactivatedEvent}.
     */
    public static UserDeactivatedEvent deactivated(final User user) {
        return new UserDeactivatedEvent(user);
    }

    /**
     * Creates a {@link UserDeletedEvent}.
     *
     * @param user The {@link User} being deleted.
     * @return The created {@link UserDeletedEvent}.
     */
    public static UserDeletedEvent deleted(final User user) {
        return new UserDeletedEvent(user);
    }
}
