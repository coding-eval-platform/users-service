package ar.edu.itba.cep.users_service.rest.controller.dtos;

import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.services.UserWithNoRoles;
import lombok.ToString;

/**
 * Data transfer object for {@link User}s.
 */
@ToString(doNotUseGetters = true)
public class NoRolesUserDto extends UserDto<UserWithNoRoles> {

    /**
     * Constructor.
     *
     * @param userWrapper The wrapper of a {@link User} to be, in turn, wrapped in this DTO.
     */
    public NoRolesUserDto(final UserWithNoRoles userWrapper) {
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
}
