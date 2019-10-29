package ar.edu.itba.cep.users_service.spring_data.interfaces;

import ar.edu.itba.cep.roles.Role;
import ar.edu.itba.cep.users_service.models.AuthToken;
import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.models.UserAuthToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A {@link CrudRepository} for {@link AuthToken}s.
 */
@Repository
public interface SpringDataUserAuthTokenRepository extends AbstractSpringDataAuthTokenRepository<UserAuthToken> {

    /**
     * Lists all the given {@link User}'s {@link UserAuthToken}s.
     *
     * @param user The {@link User} owning the returned {@link UserAuthToken}s.
     * @return A {@link List} containing al the given {@link User}'s {@link UserAuthToken}s.
     */
    @Query(value = "SELECT DISTINCT at " +
            "       FROM UserAuthToken at " +
            "           LEFT JOIN FETCH at.rolesAssigned" +
            "       WHERE at.user = :user" +
            "       ORDER BY at.createdAt")
    List<UserAuthToken> findByUser(@Param("user") final User user);

    /**
     * Lists all the given {@link User}'s {@link UserAuthToken}'s that contain the given {@code role}.
     *
     * @param user The {@link User} owning the returned {@link UserAuthToken}s.
     * @param role The {@link Role} to be matched.
     * @return A {@link List} containing the matched {@link UserAuthToken}s.
     */
    @Query(value = "SELECT DISTINCT at " +
            "       FROM UserAuthToken at " +
            "           LEFT JOIN FETCH at.rolesAssigned" +
            "       WHERE at.user = :user AND :role MEMBER OF at.rolesAssigned" +
            "       ORDER BY at.createdAt")
    List<UserAuthToken> findByUserAndRole(@Param("user") final User user, @Param("role") final Role role);
}
