package ar.edu.itba.cep.users_service.rest.controller.endpoints;

/**
 * Class holding the routes of the API.
 */
public class Routes {

    public static final String USERS = "/users";

    public static final String USER_BY_USERNAME = "/users/{username : .+}";

    public static final String USER_ACTUAL = "/actual-user";

    public static final String USER_CHANGE_OF_PASSWORD = "/users/{username : .+}/password";

    public static final String USER_ROLE_BY_NAME = "/users/{username : .+}/roles/{role : .+}";

    public static final String USER_ACTIVATION = "/users/{username : .+}/active";


    public static final String TOKENS = "/tokens";

    public static final String TOKEN_BY_ID = "/tokens/{id : .+}";

    public static final String TOKEN_REFRESH = "/tokens/{id : .+}/refresh";

    public static final String TOKENS_USERNAME = "/tokens/username/{username : .+}";

    public static final String TOKENS_SUBJECT = "/tokens/subject/{subject : .+}";


    public static final String TOKENS_INTERNAL = "/internal/tokens";
}
