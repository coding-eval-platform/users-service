package ar.edu.itba.cep.users_service.spring_data.config;

import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.models.UserCredential;
import ar.edu.itba.cep.users_service.repositories.UserCredentialRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A mocked {@link UserCredential} repository used to boot the application.
 * Remove when Spring Data dependencies are added.
 */
@Repository
// TODO: remove this class as Spring Data will create all the needed repositories.
public class MockedUserCredentialRepository implements UserCredentialRepository {

    @Override
    public Optional<UserCredential> findTopByUserOrderByCreatedAtDesc(User user) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteByUser(User user) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends UserCredential> S save(S entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends UserCredential> Iterable<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<UserCredential> findById(Long aLong) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean existsById(Long aLong) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Iterable<UserCredential> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Iterable<UserCredential> findAllById(Iterable<Long> longs) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteById(Long aLong) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(UserCredential entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAll(Iterable<? extends UserCredential> entities) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
