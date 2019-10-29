package ar.edu.itba.cep.users_service.repositories;

import ar.edu.itba.cep.roles.Role;
import ar.edu.itba.cep.users_service.models.SubjectAuthToken;
import ar.edu.itba.cep.users_service.models.UserAuthToken;

import java.util.List;

/**
 * A port out of the application that allows {@link UserAuthToken} persistence.
 */
public interface SubjectAuthTokenRepository extends AuthTokenRepository<SubjectAuthToken> {

    /**
     * Lists all the given {@code subject}'s {@link SubjectAuthToken}s.
     *
     * @param subject The {@code subject} owning the returned {@link SubjectAuthToken}s.
     * @return A {@link List} containing all the given {@code subject}'s {@link SubjectAuthToken}s.
     */
    List<SubjectAuthToken> getSubjectTokens(final String subject);

    /**
     * Lists all the given {@code subject}'s {@link SubjectAuthToken}'s that contain the given {@code role}.
     *
     * @param subject The {@code subject} owning the returned {@link SubjectAuthToken}s.
     * @param role    The {@link Role} to be matched.
     * @return A {@link List} containing the matched {@link SubjectAuthToken}s.
     */
    List<SubjectAuthToken> getSubjectTokensWithRole(final String subject, final Role role);
}
