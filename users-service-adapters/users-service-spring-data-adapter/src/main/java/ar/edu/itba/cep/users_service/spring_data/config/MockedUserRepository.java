package ar.edu.itba.cep.users_service.spring_data.config;

import ar.edu.itba.cep.users_service.models.User;
import ar.edu.itba.cep.users_service.repositories.UserRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * A mocked {@link ar.edu.itba.cep.users_service.models.User} repository used to boot the application.
 * Remove when Spring Data dependencies are added.
 */
@Repository
// TODO: remove this class as Spring Data will create all the needed repositories.
public class MockedUserRepository implements UserRepository {
    @Override
    public List<User> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<User> findAll(Sort sort) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<User> findAllById(Iterable<Long> longs) {
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
    public void delete(User entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends User> S save(S entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<User> findById(Long aLong) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean existsById(Long aLong) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends User> S saveAndFlush(S entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteInBatch(Iterable<User> entities) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAllInBatch() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public User getOne(Long aLong) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<User> findOne(Specification<User> spec) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<User> findAll(Specification<User> spec) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<User> findAll(Specification<User> spec, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<User> findAll(Specification<User> spec, Sort sort) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public long count(Specification<User> spec) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<User> findByUsername(String username) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean existsByUsername(String username) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
