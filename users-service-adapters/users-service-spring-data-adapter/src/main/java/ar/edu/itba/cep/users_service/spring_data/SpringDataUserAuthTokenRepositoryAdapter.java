package ar.edu.itba.cep.users_service.spring_data;

import ar.edu.itba.cep.roles.Role;
import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.models.UserAuthToken;
import ar.edu.itba.cep.users_service.repositories.UserAuthTokenRepository;
import ar.edu.itba.cep.users_service.spring_data.interfaces.SpringDataAuthTokenRepository;
import ar.edu.itba.cep.users_service.spring_data.interfaces.SpringDataUserAuthTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * A concrete implementation of a {@link UserAuthTokenRepository}
 * which acts as an adapter for a {@link SpringDataUserAuthTokenRepository}.
 */
@Repository
public class SpringDataUserAuthTokenRepositoryAdapter
        extends AbstractSpringDataAuthTokenRepositoryAdapter<UserAuthToken, SpringDataUserAuthTokenRepository>
        implements UserAuthTokenRepository {

    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataAuthTokenRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataUserAuthTokenRepositoryAdapter(final SpringDataUserAuthTokenRepository repository) {
        super(repository);
    }


    // ================================================================================================================
    // AuthTokenRepository specific methods
    // ================================================================================================================

    @Override
    public List<UserAuthToken> getUserTokens(final User user) {
        return getCrudRepository().findByUser(user);
    }

    @Override
    public List<UserAuthToken> getUserTokensWithRole(final User user, final Role role) {
        return getCrudRepository().findByUserAndRole(user, role);
    }
}
