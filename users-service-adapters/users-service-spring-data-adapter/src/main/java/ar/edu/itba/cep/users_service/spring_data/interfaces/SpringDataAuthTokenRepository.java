package ar.edu.itba.cep.users_service.spring_data.interfaces;

import ar.edu.itba.cep.users_service.models.AuthToken;
import ar.edu.itba.cep.users_service.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/**
 * A {@link CrudRepository} for {@link AuthToken}s.
 */
public interface SpringDataAuthTokenRepository extends CrudRepository<AuthToken, UUID> {

    /**
     * Lists all the given {@link User}'s {@link AuthToken}s.
     *
     * @param user The {@link User} owning the returned {@link AuthToken}s.
     * @return A {@link List} containing al the given {@link User}'s {@link AuthToken}s.
     */
    List<AuthToken> findByUser(final User user);
}
