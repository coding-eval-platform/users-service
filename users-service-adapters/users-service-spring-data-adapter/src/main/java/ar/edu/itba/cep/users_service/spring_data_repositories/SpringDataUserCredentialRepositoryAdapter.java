package ar.edu.itba.cep.users_service.spring_data_repositories;

import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.models.UserCredential;
import ar.edu.itba.cep.users_service.repositories.UserCredentialRepository;
import ar.edu.itba.cep.users_service.spring_data_repositories.spring_data_interfaces.SpringDataUserCredentialRepository;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.repositories.BasicRepositoryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * A concrete implementation of a {@link UserCredentialRepository}
 * which acts as an adapter for a {@link SpringDataUserCredentialRepository}.
 */
@Repository
public class SpringDataUserCredentialRepositoryAdapter
        implements UserCredentialRepository, BasicRepositoryAdapter<UserCredential, Long> {

    /**
     * A {@link SpringDataUserCredentialRepository} to which all operations are delegated.
     */
    private final SpringDataUserCredentialRepository repository;

    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataUserCredentialRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataUserCredentialRepositoryAdapter(final SpringDataUserCredentialRepository repository) {
        this.repository = repository;
    }


    // ================================================================================================================
    // RepositoryAdapter
    // ================================================================================================================

    @Override
    public SpringDataUserCredentialRepository getCrudRepository() {
        return repository;
    }


    // ================================================================================================================
    // UserCredentialRepository specific methods
    // ================================================================================================================

    @Override
    public Optional<UserCredential> findLastForUser(final User user) {
        return repository.findTopByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public void deleteByUser(final User user) {
        repository.deleteByUser(user);
    }
}
