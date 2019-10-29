package ar.edu.itba.cep.users_service.spring_data.interfaces;

import ar.edu.itba.cep.users_service.models.AuthToken;
import org.springframework.data.repository.CrudRepository;

/**
 * A {@link CrudRepository} for {@link AuthToken}s.
 */
public interface SpringDataAuthTokenRepository extends AbstractSpringDataAuthTokenRepository<AuthToken> {
}
