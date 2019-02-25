package ar.edu.itba.cep.users_service.spring_data_repositories.helpers;

import ar.edu.itba.cep.users_service.repositories.SortingRepository;
import com.bellotapps.webapps_commons.persistence.repository_utils.SortingData;
import com.bellotapps.webapps_commons.persistence.spring_data.SortingMapper;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * An extension of a {@link SortingRepository}
 * which implements all its methods using defaults,
 * and delegating sorted read operations to the {@link PagingAndSortingRepository} obtained
 * by the {@link #getRepository()} method.
 *
 * @param <E>  Concrete type of the entity to be managed by the repository.
 * @param <ID> Concrete type of the entity's id.
 */
public interface SortingRepositoryAdapter<E, ID>
        extends SortingRepository<E, ID>, PagingAndSortingRepositoryAdapter<E, ID> {

    /**
     * Returns all entities using the given {@code sortingData}.
     *
     * @param sortingData A {@link SortingData} object that indicates how sorting must be performed.
     * @return All entities, applying sorting according to the given {@code sortingData}.
     */
    @Override
    default Iterable<E> findAll(final SortingData sortingData) {
        return getRepository().findAll(SortingMapper.map(sortingData));
    }
}
