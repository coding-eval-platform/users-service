package ar.edu.itba.cep.users_service.rest.controller.dtos;

import ar.edu.itba.cep.users_service.models.User;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.IllegalValue;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.MissingValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static ar.edu.itba.cep.users_service.models.ValidationConstants.*;

/**
 * Data transfer object for a {@link User} creation request.
 * It includes the basic stuff needed to create a {@link User}.
 */
public class UserCreationRequestDto {

    /**
     * The {@link User}'s username.
     */
    @NotNull(message = "Username is missing.", payload = MissingValue.class)
    @Size(message = "Username too short", payload = IllegalValue.class, min = USERNAME_MIN_LENGTH)
    @Size(message = "Username too long", payload = IllegalValue.class, max = USERNAME_MAX_LENGTH)
    private final String username;

    /**
     * The {@link User}'s first password.
     */
    @NotNull(message = "Password is missing.", payload = MissingValue.class)
    @Size(message = "Password too short", payload = IllegalValue.class, min = PASSWORD_MIN_LENGTH)
    @Size(message = "Password too long", payload = IllegalValue.class, max = PASSWORD_MAX_LENGTH)
    @Pattern(message = "Password must contain a lowercase letter", payload = IllegalValue.class,
            regexp = PASSWORD_CONTAINS_LOWERCASE_REGEX
    )
    @Pattern(message = "Password must contain an uppercase letter", payload = IllegalValue.class,
            regexp = PASSWORD_CONTAINS_UPPERCASE_REGEX
    )
    @Pattern(message = "Password must contain a number", payload = IllegalValue.class,
            regexp = PASSWORD_CONTAINS_NUMBER_REGEX
    )
    @Pattern(message = "Password must contain a special character", payload = IllegalValue.class,
            regexp = PASSWORD_CONTAINS_SPECIAL_CHARACTER_REGEX
    )
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
