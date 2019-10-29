package ar.edu.itba.cep.users_service.spring_data.interfaces;

import ar.edu.itba.cep.users_service.models.AuthToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * An abstract repository for {@link AuthToken}.
 */
@Repository
public interface AbstractSpringDataAuthTokenRepository<T extends AuthToken> extends CrudRepository<T, UUID> {
}
