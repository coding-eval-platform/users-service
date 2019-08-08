package ar.edu.itba.cep.users_service.domain.events;

import ar.edu.itba.cep.users_service.models.Role;
import ar.edu.itba.cep.users_service.models.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A {@link UserEvent} that indicates that a {@link Role} was removed from a {@link User}.
 */
@Getter
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class UserRoleRemovedEvent extends UserEvent {

    /**
     * The {@link Role} being removed.
     */
    private final Role role;


    /**
     * Constructor.
     *
     * @param user The {@link User} being deleted.
     * @param role The {@link Role} being removed.
     */
    /* package */ UserRoleRemovedEvent(final User user, final Role role) {
        super(user);
        this.role = role;
    }
}
