package ar.edu.itba.cep.users_service.repositories;


import java.util.Optional;

/**
 * Interface for a repository with basic reading operations.
 *
 * @param <E>  Concrete type of the entity to be managed by the repository.
 * @param <ID> Concrete type of the entity's id.
 */
public interface ReaderRepository<E, ID> {

    /**
     * Returns the amount of entities available in the repository.
     *
     * @return The amount of entities available.
     */
    long count();

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id The id of the entity to check existence.
     * @return {@code true} if an entity with the given {@code id} exists, or {@code false} otherwise.
     * @throws IllegalArgumentException If the given {@code id} is {@code null}.
     */
    boolean existsById(final ID id) throws IllegalArgumentException;

    /**
     * Finds the entity with the given {@code id}.
     *
     * @param id The id of the entity to be returned.
     * @return An {@link Optional} that contains the entity with the given {@code id} if it exists, or empty otherwise.
     * @throws IllegalArgumentException If the given {@code id} is {@code null}.
     */
    Optional<E> findById(final ID id) throws IllegalArgumentException;

    /**
     * Returns all the entities in the repository.
     *
     * @return All the entities.
     */
    Iterable<E> findAll();
}
