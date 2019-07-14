package ar.edu.itba.cep.users_service.rest.controller.dtos;

import ar.edu.itba.cep.users_service.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

/**
 * Data transfer object for {@link User}s.
 */
@ToString(doNotUseGetters = true)
/* package */ abstract class UserDto<T> {

    /**
     * The wrapper of a {@link User} to be, in turn, wrapped in this DTO.
     */
    private final T userWrapper;

    /**
     * Constructor.
     *
     * @param userWrapper The wrapper of a {@link User} to be, in turn, wrapped in this DTO.
     */
    /* package */ UserDto(final T userWrapper) {
        this.userWrapper = userWrapper;
    }

    /**
     * @return The {@link User}'s username.
     */
    @JsonProperty(value = "username", access = JsonProperty.Access.READ_ONLY)
    public abstract String getUsername();

    /**
     * @return The {@link User}'s {@code active} flag.
     */
    @JsonProperty(value = "active", access = JsonProperty.Access.READ_ONLY)
    public abstract boolean isActive();

    /**
     * @return The wrapper of a {@link User} to be, in turn, wrapped in this DTO.
     */
    /* package */ T getUserWrapper() {
        return userWrapper;
    }
}
