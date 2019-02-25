package ar.edu.itba.cep.users_service.repositories;


import com.bellotapps.webapps_commons.persistence.repository_utils.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.PagingRequest;

/**
 * Interface for a repository with paged reading operations.
 *
 * @param <E>  Concrete type of the entity to be managed by the repository.
 * @param <ID> Concrete type of the entity's id.
 */
public interface PagingRepository<E, ID> {

    /**
     * Returns a {@link Page} of entities according to the given {@code pagingRequest}.
     *
     * @param pagingRequest The {@link PagingRequest} that indicates page number, size, sorting options, etc.
     * @return A {@link Page} of entities.
     */
    Page<E> findAll(final PagingRequest pagingRequest);
}
