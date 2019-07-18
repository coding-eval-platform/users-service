package ar.edu.itba.cep.users_service.rest.controller.dtos;

import ar.edu.itba.cep.users_service.services.RawTokenContainer;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object that wraps a {@link RawTokenContainer}, only exposing tokens (no id).
 */
public class RefreshTokenResponseDto {

    /**
     * The access token.
     */
    private final String accessToken;
    /**
     * A token to be used to refresh the access token.
     */
    private final String refreshToken;


    /**
     * Constructor.
     *
     * @param rawTokenContainer The {@link RawTokenContainer} being wrapped.
     */
    public RefreshTokenResponseDto(final RawTokenContainer rawTokenContainer) {
        this.accessToken = rawTokenContainer.getAccessToken();
        this.refreshToken = rawTokenContainer.getRefreshToken();
    }


    /**
     * @return The access token.
     */
    @JsonProperty(value = "accessToken", access = JsonProperty.Access.READ_ONLY)
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @return A token to be used to refresh the access token.
     */
    @JsonProperty(value = "refreshToken", access = JsonProperty.Access.READ_ONLY)
    public String getRefreshToken() {
        return refreshToken;
    }
}
