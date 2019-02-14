package ar.edu.itba.cep.users_service.repositories;

import ar.edu.itba.cep.users_service.models.User;
import com.bellotapps.webapps_commons.repositories.ExtendedJpaRepository;

import java.util.Optional;

/**
 * A port out of the application that allows {@link User} persistence.
 */
// TODO: remove comment when JPA is added into the project.
public interface UserRepository extends ExtendedJpaRepository<User, Long> {

    /**
     * Retrieves the {@link User} with the given {@code username}.
     *
     * @param username The {@link User}'s username.
     * @return An {@link Optional} that contains the {@link User} with the given {@code username} if it exists,
     * or empty otherwise.
     */
    Optional<User> findByUsername(final String username);

    /**
     * Checks if a {@link User} exists with the given {@code username}.
     *
     * @param username The username to check if a {@link User} exists with.
     * @return {@code true} if a {@link User} exists with the given {@code username}, or {@code false} otherwise.
     */
    boolean existsByUsername(final String username);
}
