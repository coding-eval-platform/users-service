package ar.edu.itba.cep.users_service.spring_data.interfaces;

import ar.edu.itba.cep.roles.Role;
import ar.edu.itba.cep.users_service.models.AuthToken;
import ar.edu.itba.cep.users_service.models.SubjectAuthToken;
import ar.edu.itba.cep.users_service.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A {@link CrudRepository} for {@link AuthToken}s.
 */
@Repository
public interface SpringDataSubjectAuthTokenRepository extends AbstractSpringDataAuthTokenRepository<SubjectAuthToken> {

    /**
     * Lists all the given {@link User}'s {@link SubjectAuthToken}s.
     *
     * @param subject The subject owning the returned {@link SubjectAuthToken}s.
     * @return A {@link List} containing al the given {@link User}'s {@link SubjectAuthToken}s.
     */
    @Query(value = "SELECT DISTINCT at " +
            "       FROM SubjectAuthToken at " +
            "           LEFT JOIN FETCH at.rolesAssigned" +
            "       WHERE at.subject = :subject" +
            "       ORDER BY at.createdAt")
    List<SubjectAuthToken> findBySubject(@Param("subject") final String subject);

    /**
     * Lists all the given {@link User}'s {@link SubjectAuthToken}'s that contain the given {@code role}.
     *
     * @param subject The subject owning the returned {@link SubjectAuthToken}s.
     * @param role    The {@link Role} to be matched.
     * @return A {@link List} containing the matched {@link SubjectAuthToken}s.
     */
    @Query(value = "SELECT DISTINCT at " +
            "       FROM SubjectAuthToken at " +
            "           LEFT JOIN FETCH at.rolesAssigned" +
            "       WHERE at.subject = :subject AND :role MEMBER OF at.rolesAssigned" +
            "       ORDER BY at.createdAt")
    List<SubjectAuthToken> findBySubjectAndRole(@Param("subject") final String subject, @Param("role") final Role role);
}
