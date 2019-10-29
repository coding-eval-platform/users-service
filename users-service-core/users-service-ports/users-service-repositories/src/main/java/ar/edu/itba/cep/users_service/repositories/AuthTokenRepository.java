package ar.edu.itba.cep.users_service.repositories;

import ar.edu.itba.cep.users_service.models.AuthToken;
import com.bellotapps.webapps_commons.persistence.repository_utils.repositories.BasicRepository;

import java.util.UUID;

/**
 * A port out of the application that allows {@link AuthToken} persistence.
 */
public interface AuthTokenRepository<T extends AuthToken> extends BasicRepository<T, UUID> {
}
