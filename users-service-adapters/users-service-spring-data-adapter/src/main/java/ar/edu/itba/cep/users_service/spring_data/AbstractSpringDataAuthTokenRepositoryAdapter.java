package ar.edu.itba.cep.users_service.spring_data;

import ar.edu.itba.cep.users_service.models.AuthToken;
import ar.edu.itba.cep.users_service.repositories.AuthTokenRepository;
import ar.edu.itba.cep.users_service.spring_data.interfaces.AbstractSpringDataAuthTokenRepository;
import ar.edu.itba.cep.users_service.spring_data.interfaces.SpringDataAuthTokenRepository;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.repositories.BasicRepositoryAdapter;
import lombok.AllArgsConstructor;

import java.util.UUID;


/**
 * An abstract repository adapter for {@link AuthToken}.
 */
@AllArgsConstructor
public abstract class AbstractSpringDataAuthTokenRepositoryAdapter<T extends AuthToken, R extends AbstractSpringDataAuthTokenRepository<T>>
        implements AuthTokenRepository<T>, BasicRepositoryAdapter<T, UUID> {

    /**
     * A {@link SpringDataAuthTokenRepository} to which all operations are delegated.
     */
    private final R repository;


    @Override
    public R getCrudRepository() {
        return repository;
    }
}
