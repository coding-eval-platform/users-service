package ar.edu.itba.cep.users_service.spring_data_repositories.helpers;

import ar.edu.itba.cep.users_service.repositories.PagingRepository;
import com.bellotapps.webapps_commons.persistence.repository_utils.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.PagingRequest;
import com.bellotapps.webapps_commons.persistence.spring_data.PagingMapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * An extension of a {@link PagingRepository}
 * which implements all its methods using defaults,
 * and delegating paged read operations to the {@link PagingAndSortingRepository} obtained
 * by the {@link #getRepository()} method.
 *
 * @param <E>  Concrete type of the entity to be managed by the repository.
 * @param <ID> Concrete type of the entity's id.
 */
public interface PagingRepositoryAdapter<E, ID>
        extends PagingRepository<E, ID>, PagingAndSortingRepositoryAdapter<E, ID> {

    /**
     * Provides the repository to which write operations are delegated.
     *
     * @return a {@link CrudRepository} to which write operations are delegated.
     */
    PagingAndSortingRepository<E, ID> getRepository();

    /**
     * Returns a {@link Page} of entities according to the given {@code pagingRequest}.
     *
     * @param pagingRequest The {@link PagingRequest} that indicates page number, size, sorting options, etc.
     * @return A {@link Page} of entities.
     */
    @Override
    default Page<E> findAll(final PagingRequest pagingRequest) {
        final var pageable = PagingMapper.map(pagingRequest);
        final var page = getRepository().findAll(pageable);
        return PagingMapper.map(page);
    }
}
