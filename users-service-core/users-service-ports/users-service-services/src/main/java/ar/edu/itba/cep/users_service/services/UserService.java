package ar.edu.itba.cep.users_service.services;

import ar.edu.itba.cep.users_service.models.Role;
import ar.edu.itba.cep.users_service.models.User;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.exceptions.UnauthorizedException;
import com.bellotapps.webapps_commons.exceptions.UniqueViolationException;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.Page;
import com.bellotapps.webapps_commons.persistence.repository_utils.paging_and_sorting.PagingRequest;

import java.util.Optional;

/**
 * A port into the application that allows {@link User} management.
 */
public interface UserService {

    /**
     * Finds {@link User}s, applying optional filters and pagination. String filters are compared as
     * with the "like" keyword, matching anywhere.
     *
     * @param username      A filter for the {@link User}'s username.
     * @param active        The {@link User}'s active flag state.
     * @param pagingRequest A {@link PagingRequest} with the paging information.
     * @return The resulting {@link Page}.
     * @apiNote Those parameter that are {@code null} will not be taken into account (they are
     * optional).
     */
    Page<UserWithNoRoles> findMatching(
            final String username,
            final Boolean active,
            final PagingRequest pagingRequest
    );

    /**
     * Retrieves the {@link User} with the given {@code username}.
     *
     * @param username The {@link User}'s username.
     * @return An {@link Optional} that contains the {@link User} with the given {@code username} if it exists,
     * or empty otherwise.
     */
    Optional<UserWithRoles> getByUsername(final String username);

    /**
     * Retrieves information about the currently authenticated {@link User}.
     *
     * @return An {@link Optional} that contains the currently authenticated {@link User} if it exists,
     * or empty otherwise.
     * @apiNote The returned type is an {@link Optional} because, the currently authenticated {@link User} might not
     * exist in the underlying repository (as authentication is stateless). However, this is an unusual situation.
     */
    Optional<UserWithRoles> getActualUser();

    /**
     * Creates a new {@link User}.
     *
     * @param username The username for the {@link User}.
     * @param password The password for the {@link User}.
     * @return The created {@link User}.
     * @throws UniqueViolationException If the given {@code username} is already in use.
     * @throws IllegalArgumentException If the username or passwords are not valid.
     */
    UserWithNoRoles register(final String username, final String password)
            throws UniqueViolationException, IllegalArgumentException;

    /**
     * Changes the password to the {@link User} with the given {@code username}.
     *
     * @param username        The {@link User}'s username.
     * @param currentPassword The {@link User}'s current password.
     * @param newPassword     The new password.
     * @throws NoSuchEntityException If there is no {@link User} with the given {@code username}.
     * @throws UnauthorizedException If the {@code currentPassword} does not match with the actual
     *                               password
     */
    void changePassword(
            final String username,
            final String currentPassword,
            final String newPassword)
            throws NoSuchEntityException, UnauthorizedException, IllegalArgumentException;

    /**
     * Adds a {@link Role} to the {@link User} with the given {@code username}.
     *
     * @param username The {@link User}'s username.
     * @param role     The {@link Role} to be added.
     * @throws NoSuchEntityException    If there is no {@link User} with the given {@code username}.
     * @throws IllegalArgumentException If the {@link Role} is invalid.
     */
    void addRole(final String username, final Role role)
            throws NoSuchEntityException, IllegalArgumentException;

    /**
     * Removes a {@link Role} from the {@link User} with the given {@code username}.
     *
     * @param username The {@link User}'s username.
     * @param role     The {@link Role} to be removed.
     * @throws NoSuchEntityException If there is no {@link User} with the given {@code username}.
     */
    void removeRole(final String username, final Role role) throws NoSuchEntityException;

    /**
     * Activates the {@link User} with the given {@code username}.
     *
     * @param username The username of the {@link User} to be activated.
     * @throws NoSuchEntityException If there is no {@link User} with the given {@code username}.
     * @apiNote This is an idempotent operation.
     */
    void activate(final String username) throws NoSuchEntityException;

    /**
     * Deactivates the {@link User} with the given {@code username}.
     *
     * @param username The username of the {@link User} to be deactivated.
     * @throws NoSuchEntityException If there is no {@link User} with the given {@code username}.
     * @apiNote This is an idempotent operation.
     */
    void deactivate(final String username) throws NoSuchEntityException;

    /**
     * Deletes the {@link User} with the given {@code username}.
     *
     * @param username The username of the {@link User} to be deleted.
     * @apiNote This is an idempotent operation.
     */
    void delete(final String username);
}
