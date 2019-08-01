package ar.edu.itba.cep.users_service.security.authorization;

import ar.edu.itba.cep.users_service.models.AuthToken;
import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.repositories.AuthTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * A component in charge of stating whether an {@link AuthToken}
 * is owned by a {@link User} with a given {@code username}.
 */
@Component(value = "authTokenAuthorizationProvider")
public class AuthTokenAuthorizationProvider {

    /**
     * The {@link AuthTokenRepository} used to load {@link AuthToken}s by their ids.
     */
    private final AuthTokenRepository authTokenRepository;


    /**
     * Constructor.
     *
     * @param authTokenRepository The {@link AuthTokenRepository} used to load {@link AuthToken}s by their ids.
     */
    @Autowired
    public AuthTokenAuthorizationProvider(final AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }


    /**
     * Indicates whether the {@link User} owning the {@link AuthToken} with the given {@code tokenId} matches its
     * username with the given {@code principal}.
     *
     * @param tokenId   The id of the {@link AuthToken} being accessed.
     * @param principal The username of the {@link User} used to check ownership.
     * @return {@code true} if the {@link AuthToken} with the given {@code tokenId} belongs to the {@link User}
     * whose username is the given {@code principal}.
     */
    @Transactional(readOnly = true)
    public boolean isOwner(final UUID tokenId, final String principal) {
        return authTokenRepository.findById(tokenId)
                .map(AuthToken::getUser)
                .map(User::getUsername)
                .filter(username -> username.equals(principal))
                .isPresent()
                ;
    }
}
