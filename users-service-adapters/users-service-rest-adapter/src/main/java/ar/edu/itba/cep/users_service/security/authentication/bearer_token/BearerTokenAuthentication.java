package ar.edu.itba.cep.users_service.security.authentication.bearer_token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * An {@link org.springframework.security.core.Authentication} created from a Bearer token, which includes
 * the username as a principal, the authorities granted in the said token, and the token's id.
 */
public class BearerTokenAuthentication extends AbstractAuthenticationToken {

    /**
     * The id of the token.
     */
    private final UUID tokenId;

    /**
     * The username of the user to which this JWT credentials belong.
     */
    private final String username;


    /**
     * Constructor.
     *
     * @param tokenId  The id of the token.
     * @param username The username of the user to which this JWT credentials belong.
     * @param grants   The grants given to the user when using this credential instance.
     */
    public BearerTokenAuthentication(
            final UUID tokenId,
            final String username,
            final Collection<String> grants) {
        super(grants.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
        this.tokenId = tokenId;
        this.username = username;
    }


    /**
     * Authenticates this token. This is a convenient method for {@code setAuthenticated(true)}.
     *
     * @see org.springframework.security.core.Authentication#setAuthenticated(boolean)
     */
    /* package */ void authenticate() {
        this.setAuthenticated(true);
    }

    /**
     * @return The id of the token.
     */
    public UUID getTokenId() {
        return tokenId;
    }

    /**
     * A convenient method to obtain the username.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        Assert.state(isAuthenticated(), "Not yet authenticated");
        return username;
    }

    @Override
    public void setAuthenticated(final boolean authenticated) {
        Assert.state(authenticated || !isAuthenticated(), "Can't undo authentication");
        super.setAuthenticated(authenticated);
    }
}
