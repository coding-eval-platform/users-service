package ar.edu.itba.cep.users_service.spring_data.interfaces;

import ar.edu.itba.cep.roles.Role;
import ar.edu.itba.cep.users_service.models.User;
import com.bellotapps.webapps_commons.persistence.spring_data.ExtendedJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * An {@link ExtendedJpaRepository} for {@link User}s.
 */
@Repository
public interface SpringDataUserRepository extends ExtendedJpaRepository<User, Long> {

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

    /**
     * Indicates whether a {@link User} exists with the given {@code role}.
     *
     * @param role The {@link Role} to be checked.
     * @return {@code true} if there is a {@link User} with the given {@code role}, or {@code false} otherwise.
     */
    boolean existsByRolesContains(final Role role);
}
