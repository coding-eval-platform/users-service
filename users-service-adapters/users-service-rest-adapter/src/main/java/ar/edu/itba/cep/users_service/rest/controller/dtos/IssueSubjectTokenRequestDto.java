package ar.edu.itba.cep.users_service.rest.controller.dtos;

import ar.edu.itba.cep.roles.Role;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.MissingValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * A Data Transfer Object with the needed information
 * to issue a new {@link ar.edu.itba.cep.users_service.models.SubjectAuthToken}.
 */
@Getter
@ToString(doNotUseGetters = true)
public class IssueSubjectTokenRequestDto {

    /**
     * The username.
     */
    @NotNull(message = "Subject is missing.", payload = ConstraintViolationError.ErrorCausePayload.MissingValue.class)
    private final String subject;
    /**
     * The password.
     */
    private final Set<@NotNull(message = "Null role.", payload = MissingValue.class) Role> roles;

    /**
     * Constructor.
     *
     * @param subject The subject.
     * @param roles   The roles.
     */
    @JsonCreator
    public IssueSubjectTokenRequestDto(
            @JsonProperty(value = "subject", access = JsonProperty.Access.WRITE_ONLY) final String subject,
            @JsonProperty(value = "roles", access = JsonProperty.Access.WRITE_ONLY) final Set<Role> roles) {
        this.subject = subject;
        this.roles = roles;
    }
}
