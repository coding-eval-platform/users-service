package ar.edu.itba.cep.users_service.repositories;

/**
 * Interface for a basic repository of a specific type.
 *
 * @param <E>  Concrete type of the entity to be managed by the repository.
 * @param <ID> Concrete type of the entity's id.
 */
public interface BasicRepository<E, ID> extends ReaderRepository<E, ID>, WriterRepository<E, ID> {
}
