package ar.edu.itba.cep.users_service.spring_data.interfaces;

import ar.edu.itba.cep.users_service.models.AuthToken;
import ar.edu.itba.cep.roles.Role;
import ar.edu.itba.cep.users_service.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * A {@link CrudRepository} for {@link AuthToken}s.
 */
public interface SpringDataAuthTokenRepository extends CrudRepository<AuthToken, UUID> {

    /**
     * Lists all the given {@link User}'s {@link AuthToken}s.
     *
     * @param user The {@link User} owning the returned {@link AuthToken}s.
     * @return A {@link List} containing al the given {@link User}'s {@link AuthToken}s.
     */
    @Query(value = "SELECT DISTINCT at " +
            "       FROM AuthToken at " +
            "           LEFT JOIN FETCH at.rolesAssigned" +
            "       WHERE at.user = :user" +
            "       ORDER BY at.createdAt")
    List<AuthToken> findByUser(@Param("user") final User user);

    /**
     * Lists all the given {@link User}'s {@link AuthToken}'s that contain the given {@code role}.
     *
     * @param user The {@link User} owning the returned {@link AuthToken}s.
     * @param role The {@link Role} to be matched.
     * @return A {@link List} containing the matched {@link AuthToken}s.
     */
    @Query(value = "SELECT DISTINCT at " +
            "       FROM AuthToken at " +
            "           LEFT JOIN FETCH at.rolesAssigned" +
            "       WHERE at.user = :user AND :role MEMBER OF at.rolesAssigned" +
            "       ORDER BY at.createdAt")
    List<AuthToken> findByUserAndRole(@Param("user") final User user, @Param("role") final Role role);
}
