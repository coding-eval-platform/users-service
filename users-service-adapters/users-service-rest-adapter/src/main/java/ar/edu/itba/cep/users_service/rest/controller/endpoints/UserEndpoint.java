package ar.edu.itba.cep.users_service.rest.controller.endpoints;

import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * API endpoint for {@link User} management.
 */
@Component
public class UserEndpoint {

    /**
     * The {@link UserService} that will be used to manage {@link User}s.
     */
    private final UserService userService;

    /**
     * Constructor.
     *
     * @param userService The {@link UserService} that will be used to manage {@link User}s.
     */
    @Autowired
    public UserEndpoint(final UserService userService) {
        this.userService = userService;
    }
}
