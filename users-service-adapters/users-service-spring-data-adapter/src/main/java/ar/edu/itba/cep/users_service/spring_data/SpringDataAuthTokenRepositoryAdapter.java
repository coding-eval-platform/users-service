package ar.edu.itba.cep.users_service.spring_data;

import ar.edu.itba.cep.users_service.models.AuthToken;
import ar.edu.itba.cep.users_service.repositories.AuthTokenRepository;
import ar.edu.itba.cep.users_service.spring_data.interfaces.SpringDataAuthTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * A concrete implementation of a {@link AuthTokenRepository}
 * which acts as an adapter for a {@link SpringDataAuthTokenRepository}.
 */
@Repository
public class SpringDataAuthTokenRepositoryAdapter
        extends AbstractSpringDataAuthTokenRepositoryAdapter<AuthToken, SpringDataAuthTokenRepository> {

    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataAuthTokenRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataAuthTokenRepositoryAdapter(final SpringDataAuthTokenRepository repository) {
        super(repository);
    }
}
