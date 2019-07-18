package ar.edu.itba.cep.users_service.rest.controller.dtos;

import ar.edu.itba.cep.users_service.models.AuthToken;
import ar.edu.itba.cep.users_service.models.Role;
import ar.edu.itba.cep.users_service.models.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object that wraps an {@link AuthToken}, exposing all of its data, except the {@link User}.
 */
public class AuthTokenDto {

    /**
     * The token's id.
     */
    private final UUID id;
    /**
     * The {@link Role}s assigned to the token
     * (i.e what the {@link User} is allowed to do when presenting this token).
     */
    private final Set<Role> rolesAssigned;
    /**
     * The {@link Instant} in which this token is created.
     */
    private final Instant createdAt;
    /**
     * A flag indicating whether this token is valid.
     */
    private final boolean valid;


    /**
     * Constructor.
     *
     * @param authToken The {@link AuthToken} being wrapped.
     */
    public AuthTokenDto(final AuthToken authToken) {
        this.id = authToken.getId();
        this.rolesAssigned = authToken.getRolesAssigned();
        this.createdAt = authToken.getCreatedAt();
        this.valid = authToken.isValid();
    }


    /**
     * @return The token's id.
     */
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    public UUID getId() {
        return id;
    }

    /**
     * @return The {@link Role}s assigned to the token
     * (i.e what the {@link User} is allowed to do when presenting this token).
     */
    @JsonProperty(value = "rolesAssigned", access = JsonProperty.Access.READ_ONLY)
    public Set<Role> getRolesAssigned() {
        return rolesAssigned;
    }

    /**
     * @return The {@link Instant} in which this token is created.
     */
    @JsonProperty(value = "createdAt", access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "UTC")
//    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ",timezone = "UTC")
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * @return A flag indicating whether this token is valid.
     */
    @JsonProperty(value = "valid", access = JsonProperty.Access.READ_ONLY)
    public boolean isValid() {
        return valid;
    }
}
