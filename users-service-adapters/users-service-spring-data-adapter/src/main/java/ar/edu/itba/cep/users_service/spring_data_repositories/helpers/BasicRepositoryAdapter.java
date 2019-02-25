package ar.edu.itba.cep.users_service.spring_data_repositories.helpers;

import ar.edu.itba.cep.users_service.repositories.BasicRepository;

/**
 * An extension of a {@link BasicRepository}
 * which also extends a {@link ReaderRepositoryAdapter} and a {@link WriterRepositoryAdapter}.
 *
 * @param <E>  Concrete type of the entity to be managed by the repository.
 * @param <ID> Concrete type of the entity's id.
 */
public interface BasicRepositoryAdapter<E, ID>
        extends BasicRepository<E, ID>, ReaderRepositoryAdapter<E, ID>, WriterRepositoryAdapter<E, ID> {
}
