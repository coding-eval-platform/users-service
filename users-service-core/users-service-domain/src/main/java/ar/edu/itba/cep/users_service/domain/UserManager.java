package ar.edu.itba.cep.users_service.domain;

import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.repositories.UserRepository;
import ar.edu.itba.cep.users_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Manager for {@link User}s.
 */
@Service
public class UserManager implements UserService {

    /**
     * Repository for {@link User}s.
     */
    private final UserRepository userRepository;

    /**
     * Constructor.
     *
     * @param userRepository Repository for {@link User}s.
     */
    @Autowired
    public UserManager(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
