package ar.edu.itba.cep.users_service.models;

/**
 * Constants to be used when validating entities.
 */
public class ValidationConstants {

    // ==================================
    // Minimum values
    // ==================================
    public final static int USERNAME_MIN_LENGTH = 1;

    public final static int PASSWORD_MIN_LENGTH = 8;

    // ==================================
    // Maximum values
    // ==================================
    public final static int USERNAME_MAX_LENGTH = 64;

    public final static int PASSWORD_MAX_LENGTH = 1024;


    // ==================================
    // Regular expressions
    // ==================================
    /**
     * A regex to check whether the password contains at least a lowercase letter.
     */
    public static final String PASSWORD_CONTAINS_LOWERCASE_REGEX = "^.*[a-z].*$";
    /**
     * A regex to check whether the password contains at least an uppercase letter.
     */
    public static final String PASSWORD_CONTAINS_UPPERCASE_REGEX = "^.*[A-Z].*$";
    /**
     * A regex to check whether the password contains at least an number.
     */
    public static final String PASSWORD_CONTAINS_NUMBER_REGEX = "^.*\\d.*$";
    /**
     * A regex to check whether the password contains at least a special character.
     */
    public static final String PASSWORD_CONTAINS_SPECIAL_CHARACTER_REGEX = "^.*[^a-zA-Z0-9].*$";
}
