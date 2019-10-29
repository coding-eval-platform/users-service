package ar.edu.itba.cep.users_service.security.authorization;

import ar.edu.itba.cep.users_service.models.AuthToken;
import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.repositories.AuthTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * A component in charge of stating whether an {@link AuthToken}
 * is owned by a {@link User} with a given {@code username}.
 */
@AllArgsConstructor
@Component(value = "authTokenAuthorizationProvider")
public class AuthTokenAuthorizationProvider {

    /**
     * The {@link AuthTokenRepository} used to load {@link AuthToken}s by their ids.
     */
    private final AuthTokenRepository<AuthToken> authTokenRepository;


    /**
     * Indicates whether the one owning the {@link AuthToken} with the given {@code tokenId} matches
     * with the given {@code principal}.
     *
     * @param tokenId   The id of the {@link AuthToken} being accessed.
     * @param principal The username/subject used to check ownership.
     * @return {@code true} if the {@link AuthToken} with the given {@code tokenId} belongs to the user/subject
     * whose username is the given {@code principal}.
     */
    @Transactional(readOnly = true)
    public boolean isOwner(final UUID tokenId, final String principal) {
        return authTokenRepository.findById(tokenId)
                .map(AuthToken::getOwner)
                .filter(principal::equals)
                .isPresent()
                ;
    }
}
