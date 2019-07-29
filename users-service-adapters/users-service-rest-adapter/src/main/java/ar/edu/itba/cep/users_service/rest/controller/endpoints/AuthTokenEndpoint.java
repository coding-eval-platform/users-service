package ar.edu.itba.cep.users_service.rest.controller.endpoints;

import ar.edu.itba.cep.users_service.models.AuthToken;
import ar.edu.itba.cep.users_service.rest.controller.dtos.AuthTokenDto;
import ar.edu.itba.cep.users_service.rest.controller.dtos.IssueTokenRequestDto;
import ar.edu.itba.cep.users_service.rest.controller.dtos.RefreshTokenResponseDto;
import ar.edu.itba.cep.users_service.services.AuthTokenService;
import com.bellotapps.webapps_commons.config.JerseyController;
import com.bellotapps.webapps_commons.exceptions.IllegalParamValueException;
import com.bellotapps.webapps_commons.exceptions.MissingJsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Rest Adapter of {@link AuthTokenService}, encapsulating {@link AuthToken} management.
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
public class AuthTokenEndpoint {

    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenService.class);

    /**
     * The adapted {@link AuthTokenService}.
     */
    private final AuthTokenService authTokenService;

    /**
     * Constructor.
     *
     * @param authTokenService The adapted {@link AuthTokenService}.
     */
    @Autowired
    public AuthTokenEndpoint(final AuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }


    @POST
    @Path(Routes.TOKENS)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response issueToken(@Context final UriInfo uriInfo, @Valid final IssueTokenRequestDto dto) {
        if (dto == null) {
            throw new MissingJsonException();
        }
        LOGGER.debug("Issuing token for user with username {}", dto.getUsername());
        final var tokenWrapper = authTokenService.issueToken(dto.getUsername(), dto.getPassword());
        final var location = uriInfo.getAbsolutePathBuilder().path(tokenWrapper.getId().toString()).build();
        return Response.created(location).entity(tokenWrapper).build();
    }

    @PUT
    @Path(Routes.TOKEN_REFRESH)
    public Response refreshToken(@PathParam("id") final UUID id) {
        if (id == null) {
            throw new IllegalParamValueException(List.of("id"));
        }
        LOGGER.debug("Refreshing token with id {}", id);
        final var tokenWrapper = authTokenService.refreshToken(id);
        return Response.ok(new RefreshTokenResponseDto(tokenWrapper)).build();
    }

    @DELETE
    @Path(Routes.TOKEN_BY_ID)
    public Response blacklistToken(@PathParam("id") final UUID id) {
        if (id == null) {
            throw new IllegalParamValueException(List.of("id"));
        }
        LOGGER.debug("Blacklisting token with id {}", id);
        authTokenService.blacklistToken(id);
        return Response.noContent().build();
    }

    @GET
    @Path(Routes.TOKENS)
    public Response listTokens(@QueryParam("username") final String username) {
        if (username == null) {
            throw new IllegalParamValueException(List.of("username"));
        }
        LOGGER.debug("Listing tokens of user with username {}", username);
        final var tokens = authTokenService.listTokens(username)
                .stream()
                .map(AuthTokenDto::new)
                .collect(Collectors.toList());
        return Response.ok(tokens).build();
    }
}
