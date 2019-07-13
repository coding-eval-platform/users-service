package ar.edu.itba.cep.users_service.rest.controller.dtos;

import ar.edu.itba.cep.users_service.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

/**
 * Data transfer object for {@link User}s.
 */
@ToString(doNotUseGetters = true)
public class UserDto {

    /**
     * The {@link User}'s username.
     */
    private final String username;

    /**
     * The {@link User}'s {@code active} flag.
     */
    private final boolean active;

    /**
     * Constructor.
     *
     * @param user The {@link User} to be wrapped in this DTO.
     */
    public UserDto(final User user) {
        this.username = user.getUsername();
        this.active = user.isActive();
    }

    /**
     * @return The {@link User}'s username.
     */
    @JsonProperty(value = "username", access = JsonProperty.Access.READ_ONLY)
    public String getUsername() {
        return username;
    }

    /**
     * @return The {@link User}'s {@code active} flag.
     */
    @JsonProperty(value = "active", access = JsonProperty.Access.READ_ONLY)
    public boolean getActive() {
        return active;
    }
}
