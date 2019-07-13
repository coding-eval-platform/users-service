package ar.edu.itba.cep.users_service.rest.controller.dtos;


import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.IllegalValue;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.MissingValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static ar.edu.itba.cep.users_service.models.ValidationConstants.*;

/**
 * Data transfer object for a change of password request.
 */
@Getter
@ToString(doNotUseGetters = true)
public class PasswordChangeRequestDto {

    /**
     * The current password (the one to be changed).
     */
    private final String currentPassword;

    /**
     * The new password (the one to be set).
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
    private final String newPassword;

    /**
     * Constructor.
     *
     * @param currentPassword The current password (the one to be changed).
     * @param newPassword     The new password (the one to be set).
     */
    @JsonCreator
    public PasswordChangeRequestDto(
            @JsonProperty(value = "currentPassword", access = JsonProperty.Access.WRITE_ONLY) final String currentPassword,
            @JsonProperty(value = "newPassword", access = JsonProperty.Access.WRITE_ONLY) final String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}
