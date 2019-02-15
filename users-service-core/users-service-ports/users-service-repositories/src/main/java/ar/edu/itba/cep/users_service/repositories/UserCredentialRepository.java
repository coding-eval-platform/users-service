package ar.edu.itba.cep.users_service.repositories;

import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.models.UserCredential;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * A port out of the application that allows {@link UserCredential} persistence.
 */
public interface UserCredentialRepository extends CrudRepository<UserCredential, Long> {

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
