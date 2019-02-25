package ar.edu.itba.cep.users_service.spring_data_repositories;

import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.models.UserCredential;
import ar.edu.itba.cep.users_service.repositories.UserCredentialRepository;
import ar.edu.itba.cep.users_service.repositories.UserRepository;
import ar.edu.itba.cep.users_service.spring_data_repositories.helpers.BasicRepositoryAdapter;
import ar.edu.itba.cep.users_service.spring_data_repositories.spring_data_interfaces.SpringDataUserCredentialRepository;
import ar.edu.itba.cep.users_service.spring_data_repositories.spring_data_interfaces.SpringDataUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * A concrete implementation of a {@link UserRepository}
 * which acts as an adapter for a {@link SpringDataUserRepository}.
 */
@Repository
public class SpringDataUserCredentialRepositoryAdapter
        implements UserCredentialRepository, BasicRepositoryAdapter<UserCredential, Long> {

    /**
     * A {@link SpringDataUserRepository} to which all operations are delegated.
     */
    private final SpringDataUserCredentialRepository repository;

    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataUserRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataUserCredentialRepositoryAdapter(final SpringDataUserCredentialRepository repository) {
        this.repository = repository;
    }


    // ================================================================================================================
    // RepositoryAdapter
    // ================================================================================================================

    @Override
    public SpringDataUserCredentialRepository getRepository() {
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
