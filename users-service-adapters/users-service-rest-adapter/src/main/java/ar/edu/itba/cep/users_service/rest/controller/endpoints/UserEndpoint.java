package ar.edu.itba.cep.users_service.rest.controller.endpoints;

import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.rest.controller.dtos.PasswordChangeRequestDto;
import ar.edu.itba.cep.users_service.rest.controller.dtos.UserCreationRequestDto;
import ar.edu.itba.cep.users_service.rest.controller.dtos.UserDto;
import ar.edu.itba.cep.users_service.services.UserService;
import com.bellotapps.webapps_commons.config.JerseyController;
import com.bellotapps.webapps_commons.data_transfer.date_time.DateTimeFormatters;
import com.bellotapps.webapps_commons.data_transfer.jersey.annotations.Java8Time;
import com.bellotapps.webapps_commons.data_transfer.jersey.annotations.PaginationParam;
import com.bellotapps.webapps_commons.exceptions.IllegalParamValueException;
import com.bellotapps.webapps_commons.exceptions.MissingJsonException;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.util.Collections;

/**
 * API endpoint for {@link User} management.
 */
@Path(UserEndpoint.USERS_ENDPOINT)
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
public class UserEndpoint {

    /**
     * Path prefix for {@link User} management.
     */
    public static final String USERS_ENDPOINT = "/users";

    /**
     * The {@link UserService} that will be used to manage {@link User}s.
     */
    private final UserService userService;

    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEndpoint.class);

    /**
     * Constructor.
     *
     * @param userService The {@link UserService} that will be used to manage {@link User}s.
     */
    @Autowired
    public UserEndpoint(final UserService userService) {
        this.userService = userService;
    }

    @GET
    public Response findMatching(@QueryParam("username") final String username,
                                 @QueryParam("active") final Boolean active,
                                 @SuppressWarnings("RestParamTypeInspection")
                                 @Java8Time(formatter = DateTimeFormatters.ISO_LOCAL_DATE)
                                 @QueryParam("date") final LocalDate date,
                                 @PaginationParam final PagingRequest pagingRequest) {
        LOGGER.debug("Getting users matching");
        final var users = userService.findMatching(username, active, pagingRequest)
                .map(UserDto::new)
                .content();
        return Response.ok(users).build();
    }

    @GET
    @Path("{username : .+}")
    public Response getUserByUsername(@PathParam("username") final String username) {
        if (username == null) {
            throw new IllegalParamValueException(Collections.singletonList("username"));
        }
        LOGGER.debug("Getting user by username {}", username);
        return userService.getByUsername(username)
                .map(UserDto::new)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND).entity(""))
                .build();

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(@Context final UriInfo uriInfo, @Valid final UserCreationRequestDto requestDto) {
        if (requestDto == null) {
            throw new MissingJsonException();
        }
        LOGGER.debug("Creating user with username {}", requestDto.getUsername());
        final var user = userService.register(requestDto.getUsername(), requestDto.getPassword());
        final var location = uriInfo.getAbsolutePathBuilder().path(user.getUsername()).build();
        return Response.created(location).build();
    }

    @PUT
    @Path("{username : .+}/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(
            @PathParam("username") final String username,
            @Valid final PasswordChangeRequestDto changeDto) {
        if (username == null) {
            throw new IllegalParamValueException(Collections.singletonList("username"));
        }
        if (changeDto == null) {
            throw new MissingJsonException();
        }
        LOGGER.debug("Changing password to user with username {} ", username);
        userService.changePassword(username, changeDto.getCurrentPassword(), changeDto.getNewPassword());
        return Response.noContent().build();
    }

    @PUT
    @Path("{username : .+}/active")
    public Response activateClient(@PathParam("username") final String username) {
        if (username == null) {
            throw new IllegalParamValueException(Collections.singletonList("username"));
        }
        LOGGER.debug("Activating user with username {}", username);
        userService.activate(username);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{username : .+}/active")
    public Response deactivateClient(@PathParam("username") final String username) {
        if (username == null) {
            throw new IllegalParamValueException(Collections.singletonList("username"));
        }
        LOGGER.debug("Deactivating user with username {}", username);
        userService.deactivate(username);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{username : .+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteByUsername(@PathParam("username") final String username) {
        if (username == null) {
            throw new IllegalParamValueException(Collections.singletonList("username"));
        }
        LOGGER.debug("Removing user with username {}", username);
        userService.delete(username);
        return Response.noContent().build();
    }
}
