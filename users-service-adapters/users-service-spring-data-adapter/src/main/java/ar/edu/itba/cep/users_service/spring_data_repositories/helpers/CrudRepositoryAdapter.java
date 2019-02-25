package ar.edu.itba.cep.users_service.spring_data_repositories.helpers;

import org.springframework.data.repository.CrudRepository;

/**
 * An base interface for all spring data {@link CrudRepository} adapters,
 * which defines a method that provides a {@link CrudRepository} to which operations are delegated.
 *
 * @param <E>  Concrete type of the entity to be managed by the repository.
 * @param <ID> Concrete type of the entity's id.
 */
public interface CrudRepositoryAdapter<E, ID> {

    /**
     * Provides the repository to which write operations are delegated.
     *
     * @return a {@link CrudRepository} to which write operations are delegated.
     */
    CrudRepository<E, ID> getRepository();
}
