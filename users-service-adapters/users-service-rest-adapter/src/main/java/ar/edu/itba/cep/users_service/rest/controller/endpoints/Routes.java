package ar.edu.itba.cep.users_service.rest.controller.endpoints;

/**
 * Class holding the routes of the API.
 */
public class Routes {

    public static final String USERS = "/users";

    public static final String USER_BY_USERNAME = "/users/{username : .+}";

    public static final String USER_CHANGE_OF_PASSWORD = "/users/{username : .+}/password";

    public static final String USER_ROLE_BY_NAME = "/users/{username : .+}/roles/{role : .+}";

    public static final String USER_ACTIVATION = "/users/{username : .+}/active";
}
