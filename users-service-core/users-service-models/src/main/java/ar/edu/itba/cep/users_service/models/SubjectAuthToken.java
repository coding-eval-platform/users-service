package ar.edu.itba.cep.users_service.models;

import ar.edu.itba.cep.roles.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * Represents a authentication/authorization token that can be issued to any subject.
 */
@Getter
@ToString(doNotUseGetters = true, callSuper = true)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class SubjectAuthToken extends AuthToken {

    /**
     * The {@link User} owning the token.
     */
    private final String subject;


    /**
     * Constructor that can assign a given {@link Set} of {@link Role}s.
     *
     * @param subject       The subject to which the token is being issued.
     * @param rolesAssigned The {@link Role}s assigned to the token
     *                      (i.e what the {@link User} is allowed to do when presenting this token).
     *                      Note that these {@link Role}s don't have to be the same
     *                      as the given {@code user} {@link Role}s
     *                      (for example, the {@code user} might have been granted a {@link Role} using only this
     *                      token).
     * @throws IllegalArgumentException In case any value is not a valid one.
     */
    public SubjectAuthToken(final String subject, final Set<Role> rolesAssigned) throws IllegalArgumentException {
        super(rolesAssigned);
        assertSubject(subject);

        this.subject = subject;
    }


    @Override
    public String getOwner() {
        return subject;
    }


    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code subject} is valid.
     *
     * @param subject The subject to be checked.
     * @throws IllegalArgumentException In case the subject is not a valid one.
     */
    private static void assertSubject(final String subject) throws IllegalArgumentException {
        Assert.hasText(subject, "The subject must not be blank");
    }
}
