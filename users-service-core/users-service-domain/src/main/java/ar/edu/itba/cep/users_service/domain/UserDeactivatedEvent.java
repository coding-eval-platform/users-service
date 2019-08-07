package ar.edu.itba.cep.users_service.domain;

import ar.edu.itba.cep.users_service.models.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A {@link UserEvent} that indicates that a {@link User} is being deactivated.
 */
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(callSuper = true)
final class UserDeactivatedEvent extends UserEvent {

    /**
     * Constructor.
     *
     * @param user The {@link User} being deactivated.
     */
    /* package */ UserDeactivatedEvent(final User user) {
        super(user);
    }
}
