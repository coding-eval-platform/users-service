package ar.edu.itba.cep.users_service.rest.controller.dtos;

import ar.edu.itba.cep.users_service.models.Role;
import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.services.UserWithRoles;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import java.util.Set;

/**
 * Data transfer object for {@link User}s.
 */
@ToString(doNotUseGetters = true)
public class WithRolesUserDto extends UserDto<UserWithRoles> {

    /**
     * Constructor.
     *
     * @param userWrapper The wrapper of a {@link User} to be, in turn, wrapped in this DTO.
     */
    public WithRolesUserDto(final UserWithRoles userWrapper) {
        super(userWrapper);
    }

    /**
     * @return The {@link User}'s username.
     */
    @Override
    public String getUsername() {
        return getUserWrapper().getUsername();
    }

    /**
     * @return The {@link User}'s {@code active} flag.
     */
    @Override
    public boolean isActive() {
        return getUserWrapper().isActive();
    }

    @JsonProperty(value = "roles", access = JsonProperty.Access.READ_ONLY)
    public Set<Role> getRoles() {
        return getUserWrapper().getRoles();
    }


}
