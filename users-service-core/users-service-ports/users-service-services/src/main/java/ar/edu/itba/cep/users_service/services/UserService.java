package ar.edu.itba.cep.users_service.services;

import ar.edu.itba.cep.users_service.models.User;
import com.bellotapps.webapps_commons.exceptions.CustomConstraintViolationException;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.exceptions.UniqueViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * A port into the application that allows {@link User} management.
 */
public interface UserService {

    /**
     * Finds {@link User}s, applying optional filters and pagination.
     * String filters are compared as with the "like" keyword, matching anywhere.
     *
     * @param username A filter for the {@link User}'s username.
     * @param active   The {@link User}'s active flag state.
     * @param pageable A {@link Pageable} with the paging information.
     * @return The resulting {@link Page}.
     * @apiNote Those parameter that are {@code null} will not be taken into account (they are optional).
     */
    Page<User> findMatching(final String username, final Boolean active, final Pageable pageable);

    /**
     * Retrieves the {@link User} with the given {@code username}.
     *
     * @param username The {@link User}'s username.
     * @return An {@link Optional} that contains the {@link User} with the given {@code username} if it exists,
     * or empty otherwise.
     */
    Optional<User> getByUsername(final String username);

    /**
     * Creates a new {@link User}.
     *
     * @param username The username for the {@link User}.
     * @param password The password for the {@link User}.
     * @return The created {@link User}.
     * @throws UniqueViolationException           If the given {@code username} is already in use.
     * @throws CustomConstraintViolationException If the username or passwords are not valid.
     */
    User register(final String username, final String password)
            throws UniqueViolationException, CustomConstraintViolationException;

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
