package ar.edu.itba.cep.users_service.spring_data_repositories.helpers;

import ar.edu.itba.cep.users_service.repositories.ReaderRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * An extension of a {@link ReaderRepository}
 * which implements all its methods using defaults,
 * and delegating read operations to the {@link CrudRepository} obtained
 * by the {@link #getRepository()} method.
 *
 * @param <E>  Concrete type of the entity to be managed by the repository.
 * @param <ID> Concrete type of the entity's id.
 */
public interface ReaderRepositoryAdapter<E, ID> extends ReaderRepository<E, ID>, CrudRepositoryAdapter<E, ID> {

    /**
     * Returns the amount of entities available in the repository.
     *
     * @return The amount of entities available.
     */
    @Override
    default long count() {
        return getRepository().count();
    }

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id The id of the entity to check existence.
     * @return {@code true} if an entity with the given {@code id} exists, or {@code false} otherwise.
     * @throws IllegalArgumentException If the given {@code id} is {@code null}.
     */
    @Override
    default boolean existsById(final ID id) throws IllegalArgumentException {
        return getRepository().existsById(id);
    }

    /**
     * Finds the entity with the given {@code id}.
     *
     * @param id The id of the entity to be returned.
     * @return An {@link Optional} that contains the entity with the given {@code id} if it exists, or empty otherwise.
     * @throws IllegalArgumentException If the given {@code id} is {@code null}.
     */
    @Override
    default Optional<E> findById(final ID id) throws IllegalArgumentException {
        return getRepository().findById(id);
    }

    /**
     * Returns all the entities in the repository.
     *
     * @return All the entities.
     */
    @Override
    default Iterable<E> findAll() {
        return getRepository().findAll();
    }
}
