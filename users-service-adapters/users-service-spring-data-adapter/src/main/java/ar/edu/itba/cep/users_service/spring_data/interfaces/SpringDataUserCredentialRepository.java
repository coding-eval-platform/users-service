package ar.edu.itba.cep.users_service.spring_data.interfaces;

import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.models.UserCredential;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A {@link CrudRepository} for {@link UserCredential}s.
 */
@Repository
public interface SpringDataUserCredentialRepository extends CrudRepository<UserCredential, Long> {

    /**
     * Retrieves the last {@link UserCredential} created for the given {@code user}.
     *
     * @param user The {@link User} owning the {@link UserCredential}.
     * @return The lastly created {@link UserCredential} created for the given {@code user}.
     */
    Optional<UserCredential> findTopByUserOrderByCreatedAtDesc(final User user);

    /**
     * Removes all {@link UserCredential} of a given {@link User}.
     *
     * @param user The {@link User} that owns the {@link UserCredential}s to be removed.
     */
    void deleteByUser(final User user);
}
