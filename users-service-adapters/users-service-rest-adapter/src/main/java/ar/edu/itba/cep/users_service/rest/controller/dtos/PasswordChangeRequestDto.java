package ar.edu.itba.cep.users_service.rest.controller.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object for a change of password request.
 */
public class PasswordChangeRequestDto {

    /**
     * The current password (the one to be changed).
     */
    private final String currentPassword;

    /**
     * The new password (the one to be set).
     */
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

    /**
     * @return The current password (the one to be changed).
     */
    public String getCurrentPassword() {
        return currentPassword;
    }

    /**
     * @return The new password (the one to be set).
     */
    public String getNewPassword() {
        return newPassword;
    }
}
