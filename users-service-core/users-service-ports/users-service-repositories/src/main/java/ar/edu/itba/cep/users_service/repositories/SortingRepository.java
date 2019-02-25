package ar.edu.itba.cep.users_service.repositories;


import com.bellotapps.webapps_commons.persistence.repository_utils.SortingData;

/**
 * Interface for a repository with sorted reading operations.
 *
 * @param <E>  Concrete type of the entity to be managed by the repository.
 * @param <ID> Concrete type of the entity's id.
 */
public interface SortingRepository<E, ID> {

    /**
     * Returns all entities using the given {@code sortingData}.
     *
     * @param sortingData A {@link SortingData} object that indicates how sorting must be performed.
     * @return All entities, applying sorting according to the given {@code sortingData}.
     */
    Iterable<E> findAll(final SortingData sortingData);
}
