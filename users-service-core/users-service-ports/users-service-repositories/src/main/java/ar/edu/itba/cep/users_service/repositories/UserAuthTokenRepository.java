package ar.edu.itba.cep.users_service.repositories;

import ar.edu.itba.cep.roles.Role;
import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.models.UserAuthToken;

import java.util.List;

/**
 * A port out of the application that allows {@link UserAuthToken} persistence.
 */
public interface UserAuthTokenRepository extends AuthTokenRepository<UserAuthToken> {

    /**
     * Lists all the given {@link User}'s {@link UserAuthToken}s.
     *
     * @param user The {@link User} owning the returned {@link UserAuthToken}s.
     * @return A {@link List} containing all the given {@link User}'s {@link UserAuthToken}s.
     */
    List<UserAuthToken> getUserTokens(final User user);

    /**
     * Lists all the given {@link User}'s {@link UserAuthToken}'s that contain the given {@code role}.
     *
     * @param user The {@link User} owning the returned {@link UserAuthToken}s.
     * @param role The {@link Role} to be matched.
     * @return A {@link List} containing the matched {@link UserAuthToken}s.
     */
    List<UserAuthToken> getUserTokensWithRole(final User user, final Role role);
}
