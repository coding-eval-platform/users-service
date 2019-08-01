package ar.edu.itba.cep.users_service.spring_data;

import ar.edu.itba.cep.users_service.models.Role;
import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.repositories.UserRepository;
import ar.edu.itba.cep.users_service.spring_data.interfaces.SpringDataUserRepository;
import com.bellotapps.webapps_commons.persistence.jpa.PredicateBuilders;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import com.bellotapps.webapps_commons.persistence.spring_data.PageableValidator;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.paging_and_sorting.PagingMapper;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.repositories.WriterRepositoryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Predicate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/**
 * A concrete implementation of a {@link UserRepository}
 * which acts as an adapter for a {@link SpringDataUserRepository}.
 */
@Repository
public class SpringDataUserRepositoryAdapter implements UserRepository, WriterRepositoryAdapter<User, Long> {

    /**
     * A {@link SpringDataUserRepository} to which all operations are delegated.
     */
    private final SpringDataUserRepository repository;

    /**
     * Constructor.
     *
     * @param repository A {@link SpringDataUserRepository} to which all operations are delegated.
     */
    @Autowired
    public SpringDataUserRepositoryAdapter(final SpringDataUserRepository repository) {
        this.repository = repository;
    }


    // ================================================================================================================
    // RepositoryAdapter
    // ================================================================================================================

    @Override
    public SpringDataUserRepository getCrudRepository() {
        return repository;
    }


    // ================================================================================================================
    // UserRepository specific methods
    // ================================================================================================================

    @Override
    public Optional<User> findByUsername(final String username) {
        return repository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(final String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public Page<User> findFiltering(final String username, final Boolean active, final PagingRequest pagingRequest) {
        final var pageable = PagingMapper.map(pagingRequest);
        PageableValidator.validatePageable(pageable, User.class);
        final Specification<User> specification = (root, query, cb) -> {

            final List<Predicate> predicates = new LinkedList<>();
            // Filter name
            Optional.ofNullable(username)
                    .map(str ->
                            PredicateBuilders.like(
                                    cb,
                                    root,
                                    "username",
                                    str,
                                    PredicateBuilders.LikeMatchMode.ANYWHERE,
                                    false
                            )
                    )
                    .ifPresent(predicates::add);
            Optional.ofNullable(active)
                    .map(str ->
                            PredicateBuilders.equality(
                                    cb,
                                    root,
                                    "active",
                                    Boolean.class,
                                    active
                            )
                    )
                    .ifPresent(predicates::add);
            return predicates.stream().reduce(cb.and(), cb::and);
        };
        return PagingMapper.map(repository.findAll(specification, pageable));
    }

    @Override
    public boolean existsWithRole(final Role role) {
        return repository.existsByRolesContains(role);
    }
}
