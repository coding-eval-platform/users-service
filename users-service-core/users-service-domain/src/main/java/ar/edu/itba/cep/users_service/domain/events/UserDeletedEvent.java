package ar.edu.itba.cep.users_service.domain.events;

import ar.edu.itba.cep.users_service.models.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A {@link UserEvent} that indicates that a {@link User} is being deleted.
 */
@Getter
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class UserDeletedEvent extends UserEvent {

    /**
     * Constructor.
     *
     * @param user The {@link User} being deleted.
     */
    /* package */ UserDeletedEvent(final User user) {
        super(user);
    }
}
