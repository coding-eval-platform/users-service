package ar.edu.itba.cep.users_service.spring_data;

import ar.edu.itba.cep.users_service.models.AuthToken;
import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.repositories.AuthTokenRepository;
import ar.edu.itba.cep.users_service.spring_data.interfaces.SpringDataAuthTokenRepository;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.repositories.BasicRepositoryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


/**
 * A concrete implementation of a {@link AuthTokenRepository}
 * which acts as an adapter for a {@link SpringDataAuthTokenRepository}.
 */
@Repository
public class SpringDataAuthTokenRepositoryAdapter
        implements AuthTokenRepository, BasicRepositoryAdapter<AuthToken, UUID> {

    /**
     * A {@link SpringDataAuthTokenRepository} to which all operations are delegated.
     */
    private final SpringDataAuthTokenRepository repository;

    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataAuthTokenRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataAuthTokenRepositoryAdapter(final SpringDataAuthTokenRepository repository) {
        this.repository = repository;
    }


    // ================================================================================================================
    // RepositoryAdapter
    // ================================================================================================================

    @Override
    public SpringDataAuthTokenRepository getCrudRepository() {
        return repository;
    }


    // ================================================================================================================
    // AuthTokenRepository specific methods
    // ================================================================================================================

    @Override
    public List<AuthToken> getUserTokens(final User user) {
        return repository.findByUser(user);
    }
}
