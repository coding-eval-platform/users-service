package ar.edu.itba.cep.users_service.rest.controller.endpoints;

import ar.edu.itba.cep.roles.Role;
import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.rest.controller.dtos.NoRolesUserDto;
import ar.edu.itba.cep.users_service.rest.controller.dtos.PasswordChangeRequestDto;
import ar.edu.itba.cep.users_service.rest.controller.dtos.UserCreationRequestDto;
import ar.edu.itba.cep.users_service.rest.controller.dtos.WithRolesUserDto;
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
import java.util.function.BiConsumer;

/**
 * Rest Adapter of {@link UserService}, encapsulating {@link User} management.
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
public class UserEndpoint {

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
    @Path(Routes.USERS)
    public Response findMatching(@QueryParam("username") final String username,
                                 @QueryParam("active") final Boolean active,
                                 @SuppressWarnings("RestParamTypeInspection")
                                 @Java8Time(formatter = DateTimeFormatters.ISO_LOCAL_DATE)
                                 @QueryParam("date") final LocalDate date,
                                 @PaginationParam final PagingRequest pagingRequest) {
        LOGGER.debug("Getting users matching");
        final var users = userService.findMatching(username, active, pagingRequest)
                .map(NoRolesUserDto::new)
                .content();
        return Response.ok(users).build();
    }

    @GET
    @Path(Routes.USER_BY_USERNAME)
    public Response getUserByUsername(@PathParam("username") final String username) {
        if (username == null) {
            throw new IllegalParamValueException(Collections.singletonList("username"));
        }
        LOGGER.debug("Getting user by username {}", username);
        return userService.getByUsername(username)
                .map(WithRolesUserDto::new)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND).entity(""))
                .build();
    }

    @GET
    @Path(Routes.USER_ACTUAL)
    public Response getActualUsername() {
        LOGGER.debug("Getting actual user");
        return userService.getActualUser()
                .map(WithRolesUserDto::new)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND).entity(""))
                .build();
    }

    @POST
    @Path(Routes.USERS)
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
    @Path(Routes.USER_CHANGE_OF_PASSWORD)
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
    @Path(Routes.USER_ROLE_BY_NAME)
    public Response addRole(
            @PathParam("username") final String username,
            @PathParam("role") final Role role) {
        return operateOverUser(
                username,
                (us, uName) -> us.addRole(uName, role),
                "Adding role {} to user with username {}",
                role,
                username
        );
    }

    @DELETE
    @Path(Routes.USER_ROLE_BY_NAME)
    public Response removeRole(
            @PathParam("username") final String username,
            @PathParam("role") final Role role) {
        return operateOverUser(
                username,
                (us, uName) -> us.removeRole(uName, role),
                "Removing role {} to user with username {}",
                role,
                username
        );
    }

    @PUT
    @Path(Routes.USER_ACTIVATION)
    public Response activateUser(@PathParam("username") final String username) {
        return operateOverUser(username, UserService::activate, "Activating user with username {}", username);
    }

    @DELETE
    @Path(Routes.USER_ACTIVATION)
    public Response deactivateUser(@PathParam("username") final String username) {
        return operateOverUser(username, UserService::deactivate, "Deactivating user with username {}", username);
    }

    @DELETE
    @Path(Routes.USER_BY_USERNAME)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteByUsername(@PathParam("username") final String username) {
        return operateOverUser(username, UserService::delete, "Removing user with username {}", username);

    }

    /**
     * Performs an operation over a {@link User}, returning a 204 No Content {@link Response}.
     *
     * @param username      The username of the {@link User} to which the operation will be applied.
     * @param userOperation A {@link BiConsumer} that takes the {@link UserService} and the {@code username},
     *                      and performs the operation.
     * @param message       A message to be displayed in DEBUG mode by the logger.
     * @param messageArgs   Arguments for the {@code message} to be logged.
     * @return The {@link Response}.
     */
    private Response operateOverUser(
            final String username,
            final BiConsumer<UserService, String> userOperation,
            final String message,
            final Object... messageArgs) {
        if (username == null) {
            throw new IllegalParamValueException(Collections.singletonList("username"));
        }
        LOGGER.debug(message, messageArgs);
        userOperation.accept(userService, username);
        return Response.noContent().build();
    }
}
