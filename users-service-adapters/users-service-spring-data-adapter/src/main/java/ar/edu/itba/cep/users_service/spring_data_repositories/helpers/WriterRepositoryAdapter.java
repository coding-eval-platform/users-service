package ar.edu.itba.cep.users_service.spring_data_repositories.helpers;

import ar.edu.itba.cep.users_service.repositories.WriterRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * An extension of a {@link WriterRepository}
 * which implements all its methods using defaults,
 * and delegating write operations to the {@link CrudRepository} obtained
 * by the {@link #getRepository()} method.
 *
 * @param <E>  Concrete type of the entity to be managed by the repository.
 * @param <ID> Concrete type of the entity's id.
 */
public interface WriterRepositoryAdapter<E, ID> extends WriterRepository<E, ID>, CrudRepositoryAdapter<E, ID> {

    /**
     * Saves the given {@code entity}. Use the returned entity instance for further operation.
     *
     * @param entity The entity to be saved.
     * @param <S>    Concrete type of the entity.
     * @return The saved instance.
     * @throws IllegalArgumentException If the given {@code entity} is {@code null}.
     */
    @Override
    default <S extends E> S save(final S entity) throws IllegalArgumentException {
        return getRepository().save(entity);
    }

    /**
     * Deletes the given {@code entity}.
     *
     * @param entity The entity to be deleted.
     * @param <S>    Concrete type of the entity.
     * @throws IllegalArgumentException If the given {@code entity} is {@code null}.
     */
    @Override
    default <S extends E> void delete(final S entity) throws IllegalArgumentException {
        getRepository().delete(entity);
    }

    /**
     * Deletes the entity with the given {@code id}.
     *
     * @param id The id of the entity to be deleted.
     * @throws IllegalArgumentException If the given {@code id} is {@code null}.
     */
    @Override
    default void deleteById(final ID id) throws IllegalArgumentException {
        getRepository().deleteById(id);
    }

    /**
     * Deletes all the entities in the repository.
     */
    @Override
    default void deleteAll() {
        getRepository().deleteAll();
    }
}
