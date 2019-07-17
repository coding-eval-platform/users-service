package ar.edu.itba.cep.users_service.spring_data.interfaces;

import ar.edu.itba.cep.users_service.models.AuthToken;
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
}
