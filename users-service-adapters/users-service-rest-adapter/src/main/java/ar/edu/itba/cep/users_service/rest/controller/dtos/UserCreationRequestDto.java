package ar.edu.itba.cep.users_service.rest.controller.dtos;

import ar.edu.itba.cep.users_service.models.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object for a {@link User} creation request.
 * It includes the basic stuff needed to create a {@link User}.
 */
public class UserCreationRequestDto {

    /**
     * The {@link User}'s username.
     */
    private final String username;

    /**
     * The {@link User}'s first password.
     */
    private final String password;


    /**
     * Constructor.
     *
     * @param username The {@link User}'s username.
     * @param password The {@link User}'s first password.
     */
    @JsonCreator
    public UserCreationRequestDto(
            @JsonProperty(value = "username", access = JsonProperty.Access.WRITE_ONLY) final String username,
            @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY) final String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * @return The {@link User}'s username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return The {@link User}'s first password.
     */
    public String getPassword() {
        return password;
    }
}
