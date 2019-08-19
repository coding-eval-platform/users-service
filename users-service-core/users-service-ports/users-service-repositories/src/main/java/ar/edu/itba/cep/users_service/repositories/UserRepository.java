package ar.edu.itba.cep.users_service.repositories;

import ar.edu.itba.cep.roles.Role;
import ar.edu.itba.cep.users_service.models.User;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;
import com.bellotapps.webapps_commons.persistence.repository_utils.repositories.WriterRepository;

import java.util.Optional;

/**
 * A port out of the application that allows {@link User} persistence.
 */
public interface UserRepository extends WriterRepository<User, Long> {

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
     * Searches for {@link User}s applying "like" username and active flag filters, returning data in a {@link Page}.
     *
     * @param username      A username pattern.
     * @param active        The active flag.
     * @param pagingRequest {@link PagingRequest} indicating information about page number, size and sorting data.
     * @return The {@link Page} containing {@link User}s matching.
     */
    Page<User> findFiltering(final String username, final Boolean active, final PagingRequest pagingRequest);

    /**
     * Indicates whether a {@link User} exists with the given {@code role}.
     *
     * @param role The {@link Role} to be checked.
     * @return {@code true} if there is a {@link User} with the given {@code role}, or {@code false} otherwise.
     */
    boolean existsWithRole(final Role role);
}
