package ar.edu.itba.cep.users_service.spring_data;

import ar.edu.itba.cep.roles.Role;
import ar.edu.itba.cep.users_service.models.SubjectAuthToken;
import ar.edu.itba.cep.users_service.repositories.SubjectAuthTokenRepository;
import ar.edu.itba.cep.users_service.spring_data.interfaces.SpringDataAuthTokenRepository;
import ar.edu.itba.cep.users_service.spring_data.interfaces.SpringDataSubjectAuthTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * A concrete implementation of a {@link SubjectAuthTokenRepository}
 * which acts as an adapter for a {@link SpringDataSubjectAuthTokenRepository}.
 */
@Repository
public class SpringDataSubjectAuthTokenRepositoryAdapter
        extends AbstractSpringDataAuthTokenRepositoryAdapter<SubjectAuthToken, SpringDataSubjectAuthTokenRepository>
        implements SubjectAuthTokenRepository {

    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataAuthTokenRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataSubjectAuthTokenRepositoryAdapter(final SpringDataSubjectAuthTokenRepository repository) {
        super(repository);
    }


    // ================================================================================================================
    // AuthTokenRepository specific methods
    // ================================================================================================================

    @Override
    public List<SubjectAuthToken> getSubjectTokens(final String subject) {
        return getCrudRepository().findBySubject(subject);
    }

    @Override
    public List<SubjectAuthToken> getSubjectTokensWithRole(final String subject, final Role role) {
        return getCrudRepository().findBySubjectAndRole(subject, role);
    }
}
