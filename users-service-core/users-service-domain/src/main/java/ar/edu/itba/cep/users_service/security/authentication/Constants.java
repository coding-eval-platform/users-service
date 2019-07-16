package ar.edu.itba.cep.users_service.security.authentication;

import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Constants regarding authentication.
 */
public class Constants {

    /**
     * Claims name for the roles in a jwt.
     */
    public static final String ROLES_CLAIM = "roles";

    /**
     * A "role" that allows performing a "refresh token" operation.
     */
    public static final String REFRESH_GRANT = "REFRESH";

    /**
     * Signature algorithm used to sign jwt tokens.
     */
    public static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.RS512;


    /**
     * Private constructor to avoid instantiation.
     */
    private Constants() {
    }
}
