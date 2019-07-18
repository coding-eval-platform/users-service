package ar.edu.itba.cep.users_service.repositories;

import ar.edu.itba.cep.users_service.models.AuthToken;
import ar.edu.itba.cep.users_service.models.User;
import com.bellotapps.webapps_commons.persistence.repository_utils.repositories.BasicRepository;

import java.util.List;
import java.util.UUID;

/**
 * A port out of the application that allows {@link AuthToken} persistence.
 */
public interface AuthTokenRepository extends BasicRepository<AuthToken, UUID> {

    /**
     * Lists all the given {@link User}'s {@link AuthToken}s.
     *
     * @param user The {@link User} owning the returned {@link AuthToken}s.
     * @return A {@link List} containing al the given {@link User}'s {@link AuthToken}s.
     */
    List<AuthToken> getUserTokens(final User user);
}
