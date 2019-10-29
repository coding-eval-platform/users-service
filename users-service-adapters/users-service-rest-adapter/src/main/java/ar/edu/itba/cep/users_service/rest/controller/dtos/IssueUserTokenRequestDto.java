package ar.edu.itba.cep.users_service.rest.controller.dtos;

import com.bellotapps.webapps_commons.errors.ConstraintViolationError;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * A Data Transfer Object with the needed information
 * to issue a new {@link ar.edu.itba.cep.users_service.models.UserAuthToken}.
 */
@Getter
@ToString(doNotUseGetters = true)
public class IssueUserTokenRequestDto {

    /**
     * The username.
     */
    @NotNull(message = "Username is missing.", payload = ConstraintViolationError.ErrorCausePayload.MissingValue.class)
    private final String username;
    /**
     * The password.
     */
    @NotNull(message = "Password is missing.", payload = ConstraintViolationError.ErrorCausePayload.MissingValue.class)
    private final String password;

    /**
     * Constructor.
     *
     * @param username The username.
     * @param password The password.
     */
    @JsonCreator
    public IssueUserTokenRequestDto(
            @JsonProperty(value = "username", access = JsonProperty.Access.WRITE_ONLY) final String username,
            @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY) final String password) {
        this.username = username;
        this.password = password;
    }
}
