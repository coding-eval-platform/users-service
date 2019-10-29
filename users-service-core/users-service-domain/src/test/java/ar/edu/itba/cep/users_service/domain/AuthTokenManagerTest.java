package ar.edu.itba.cep.users_service.domain;

import ar.edu.itba.cep.roles.Role;
import ar.edu.itba.cep.users_service.domain.events.UserEvent;
import ar.edu.itba.cep.users_service.domain.events.UserRoleRemovedEvent;
import ar.edu.itba.cep.users_service.models.*;
import ar.edu.itba.cep.users_service.repositories.*;
import ar.edu.itba.cep.users_service.security.authentication.TokenEncoder;
import ar.edu.itba.cep.users_service.security.authentication.TokensWrapper;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.exceptions.UnauthenticatedException;
import com.bellotapps.webapps_commons.exceptions.UnauthorizedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.*;

/**
 * Test class for the {@link AuthTokenManager}.
 */
@ExtendWith(MockitoExtension.class)
class AuthTokenManagerTest {

    private final UserRepository userRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final AuthTokenRepository<AuthToken> authTokenRepository;
    private final UserAuthTokenRepository userAuthTokenRepository;
    private final SubjectAuthTokenRepository subjectAuthTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenEncoder tokenEncoder;

    private final AuthTokenManager authTokenManager;


    /**
     * Constructor.
     *
     * @param userRepository           The {@link UserRepository} that is injected to the {@link AuthTokenManager}.
     * @param userCredentialRepository The {@link UserCredentialRepository} that is injected to the {@link AuthTokenManager}.
     * @param authTokenRepository      The {@link AuthTokenRepository} that is injected to the {@link AuthTokenManager}.
     * @param passwordEncoder          The {@link PasswordEncoder} that is injected to the {@link AuthTokenManager}.
     * @param tokenEncoder             The {@link TokenEncoder} that is injected to the {@link AuthTokenManager}.
     */
    AuthTokenManagerTest(
            @Mock(name = "userRepository") final UserRepository userRepository,
            @Mock(name = "userCredentialRepository") final UserCredentialRepository userCredentialRepository,
            @Mock(name = "authTokenRepository") final AuthTokenRepository<AuthToken> authTokenRepository,
            @Mock(name = "userAuthTokenRepository") final UserAuthTokenRepository userAuthTokenRepository,
            @Mock(name = "subjectAuthTokenRepository") final SubjectAuthTokenRepository subjectAuthTokenRepository,
            @Mock(name = "passwordEncoder") final PasswordEncoder passwordEncoder,
            @Mock(name = "tokenEncoder") final TokenEncoder tokenEncoder) {
        this.userRepository = userRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.authTokenRepository = authTokenRepository;
        this.userAuthTokenRepository = userAuthTokenRepository;
        this.subjectAuthTokenRepository = subjectAuthTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenEncoder = tokenEncoder;

        this.authTokenManager = new AuthTokenManager(
                userRepository,
                userCredentialRepository,
                authTokenRepository,
                userAuthTokenRepository,
                subjectAuthTokenRepository,
                passwordEncoder,
                tokenEncoder
        );
    }


    // ================================================================================================================
    // Happy path tests
    // ================================================================================================================

    /**
     * Tests that issuing an {@link AuthToken} works as expected.
     *
     * @param user           A mocked {@link User} (the one for which a token is being tried to be issued).
     * @param userCredential A mocked {@link UserCredential} (the one being matched).
     * @param tokensWrapper  A mocked {@link TokensWrapper} (the one being returned by the manager).
     */
    @Test
    void testIssueUserToken(
            @Mock(name = "user") final User user,
            @Mock(name = "userCredential") final UserCredential userCredential,
            @Mock(name = "tokensWrapper") final TokensWrapper tokensWrapper) {
        final var username = TestHelper.validUsername();
        final var hashedPassword = TestHelper.validPassword();
        final var inputPassword = TestHelper.validPassword() + "another";
        final Set<Role> userRoles = new HashSet<>(Arrays.asList(Role.values()));
        when(user.isActive()).thenReturn(true);
        when(user.getRoles()).thenReturn(userRoles);
        when(userCredential.getHashedPassword()).thenReturn(hashedPassword);
        when(passwordEncoder.matches(inputPassword, hashedPassword)).thenReturn(true);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userCredentialRepository.findLastForUser(user)).thenReturn(Optional.of(userCredential));
        when(userAuthTokenRepository.save(any(UserAuthToken.class))).then(i -> i.getArgument(0));
        when(tokenEncoder.encode(any(AuthToken.class))).thenReturn(tokensWrapper);

        authTokenManager.issueTokenForUser(username, inputPassword);

        verify(userRepository, only()).findByUsername(username);
        verify(userCredentialRepository, only()).findLastForUser(user);
        verify(passwordEncoder, only()).matches(inputPassword, hashedPassword);
        verify(userAuthTokenRepository, only()).save(argThat(matchingUserToken(user, userRoles)));
        verify(tokenEncoder, only()).encode(argThat(matchingUserToken(user, userRoles)));
        verifyZeroInteractions(authTokenRepository, tokenEncoder);
    }

    /**
     * Tests that issuing an {@link AuthToken} works as expected.
     *
     * @param tokensWrapper A mocked {@link TokensWrapper} (the one being returned by the manager).
     */
    @Test
    void testIssueSubjectToken(
            @Mock(name = "tokensWrapper") final TokensWrapper tokensWrapper) {
        final var subject = TestHelper.validUsername();
        final Set<Role> roles = new HashSet<>(Arrays.asList(Role.values()));

        when(subjectAuthTokenRepository.save(any(SubjectAuthToken.class))).then(i -> i.getArgument(0));
        when(tokenEncoder.encode(any(AuthToken.class))).thenReturn(tokensWrapper);

        authTokenManager.issueTokenForSubject(subject, roles);

        verify(subjectAuthTokenRepository, only()).save(argThat(matchingSubjectToken(subject, roles)));
        verify(tokenEncoder, only()).encode(argThat(matchingSubjectToken(subject, roles)));
        verifyZeroInteractions(authTokenRepository, tokenEncoder);
    }

    /**
     * Tests that refreshing an {@link UserAuthToken} works as expected.
     *
     * @param token         A mocked {@link UserAuthToken} (the one being tried to be refreshed).
     * @param tokensWrapper A mocked {@link TokensWrapper} (the one being returned by the manager).
     */
    @Test
    void testRefreshUserToken(
            @Mock(name = "token", answer = RETURNS_DEEP_STUBS) final UserAuthToken token,
            @Mock(name = "tokensWrapper") final TokensWrapper tokensWrapper) {
        final var tokenId = TestHelper.validTokenId();
        when(token.isValid()).thenReturn(true);
        when(token.getUser().isActive()).thenReturn(true);

        when(authTokenRepository.findById(tokenId)).thenReturn(Optional.of(token));
        when(tokenEncoder.encode(token)).thenReturn(tokensWrapper);

        authTokenManager.refreshToken(tokenId);

        verify(authTokenRepository, only()).findById(tokenId);
        verify(tokenEncoder, only()).encode(token);
        verifyZeroInteractions(userRepository, userCredentialRepository, passwordEncoder);
    }

    /**
     * Tests that refreshing an {@link SubjectAuthToken} works as expected.
     *
     * @param token         A mocked {@link SubjectAuthToken} (the one being tried to be refreshed).
     * @param tokensWrapper A mocked {@link TokensWrapper} (the one being returned by the manager).
     */
    @Test
    void testRefreshSubjectToken(
            @Mock(name = "token", answer = RETURNS_DEEP_STUBS) final SubjectAuthToken token,
            @Mock(name = "tokensWrapper") final TokensWrapper tokensWrapper) {
        final var tokenId = TestHelper.validTokenId();
        when(token.isValid()).thenReturn(true);

        when(authTokenRepository.findById(tokenId)).thenReturn(Optional.of(token));
        when(tokenEncoder.encode(token)).thenReturn(tokensWrapper);

        authTokenManager.refreshToken(tokenId);

        verify(authTokenRepository, only()).findById(tokenId);
        verify(tokenEncoder, only()).encode(token);
        verifyZeroInteractions(userRepository, userCredentialRepository, passwordEncoder);
    }

    /**
     * Tests that blacklisting a valid {@link AuthToken} works as expected, performing the action.
     *
     * @param token A mocked {@link AuthToken} (the one being tried to be blacklisted).
     */
    @Test
    void testBlacklistValidToken(@Mock(name = "token") final AuthToken token) {
        final var tokenId = TestHelper.validTokenId();
        when(token.isValid()).thenReturn(true);
        doNothing().when(token).invalidate();
        when(authTokenRepository.findById(tokenId)).thenReturn(Optional.of(token));
        when(authTokenRepository.save(token)).thenReturn(token);

        authTokenManager.blacklistToken(tokenId);

        verify(token, times(1)).isValid();
        verify(token, times(1)).invalidate();
        verifyNoMoreInteractions(token);
        verify(authTokenRepository, times(1)).findById(tokenId);
        verify(authTokenRepository, times(1)).save(token);
        verifyNoMoreInteractions(authTokenRepository);
        verifyZeroInteractions(userRepository, userCredentialRepository, passwordEncoder, tokenEncoder);

    }

    /**
     * Tests that blacklisting an invalid {@link AuthToken} works as expected, ignoring the action.
     *
     * @param token A mocked {@link AuthToken} (the one being tried to be blacklisted).
     */
    @Test
    void testBlacklistNonValidToken(@Mock(name = "token") final AuthToken token) {
        final var tokenId = TestHelper.validTokenId();
        when(token.isValid()).thenReturn(false);
        when(authTokenRepository.findById(tokenId)).thenReturn(Optional.of(token));

        authTokenManager.blacklistToken(tokenId);

        verify(token, only()).isValid();
        verifyOnlyTokenSearch(tokenId);
    }

    /**
     * Tests that listing the {@link UserAuthToken}s of a {@link User} works as expected.
     *
     * @param user   A mocked {@link User} (the one owning the {@link UserAuthToken}s being returned).
     * @param tokens A mocked {@link UserAuthToken} {@link List} (the one being returned).
     */
    @Test
    void testListUserTokens(
            @Mock(name = "user") final User user,
            @Mock(name = "tokens") final List<UserAuthToken> tokens) {
        final var username = TestHelper.validUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userAuthTokenRepository.getUserTokens(user)).thenReturn(tokens);

        Assertions.assertEquals(
                tokens,
                authTokenManager.listUserTokens(username),
                "The returned list of tokens of a user is not the one being returned by the underlying repository"
        );

        verify(userRepository, only()).findByUsername(username);
        verify(userAuthTokenRepository, only()).getUserTokens(user);
        verifyZeroInteractions(userCredentialRepository, passwordEncoder, tokenEncoder);
    }

    /**
     * Tests that listing the {@link SubjectAuthToken}s of a {@link User} works as expected.
     *
     * @param tokens A mocked {@link SubjectAuthToken} {@link List} (the one being returned).
     */
    @Test
    void testListSubjectTokens(@Mock(name = "tokens") final List<SubjectAuthToken> tokens) {
        final var subject = TestHelper.validUsername();
        when(subjectAuthTokenRepository.getSubjectTokens(subject)).thenReturn(tokens);

        Assertions.assertEquals(
                tokens,
                authTokenManager.listSubjectTokens(subject),
                "The returned list of tokens of a user is not the one being returned by the underlying repository"
        );

        verify(subjectAuthTokenRepository, only()).getSubjectTokens(subject);
        verifyZeroInteractions(userCredentialRepository, passwordEncoder, tokenEncoder);
    }

    /**
     * Tests that handling a {@link UserRoleRemovedEvent} by the
     * {@link AuthTokenManager#removeAllUserTokensWithRole(UserRoleRemovedEvent)} method works as expected.
     *
     * @param event  A mocked {@link UserRoleRemovedEvent} (the one being passed to the method).
     * @param user   A mocked {@link User} (the one contained in the event)
     * @param token1 A mocked {@link AuthToken} (one of the {@link AuthToken}s belonging to the {@code user}).
     * @param token2 A mocked {@link AuthToken} (one of the {@link AuthToken}s belonging to the {@code user}).
     * @param token3 A mocked {@link AuthToken} (one of the {@link AuthToken}s belonging to the {@code user}).
     */
    @Test
    void testRemoveAllUserTokensWithRole(
            @Mock(name = "event") final UserRoleRemovedEvent event,
            @Mock(name = "user") final User user,
            @Mock(name = "token1") final UserAuthToken token1,
            @Mock(name = "token2") final UserAuthToken token2,
            @Mock(name = "token3") final UserAuthToken token3) {
        final var role = TestHelper.randomRole();
        when(event.getUser()).thenReturn(user);
        when(event.getRole()).thenReturn(role);
        when(token1.isValid()).thenReturn(true);
        when(token2.isValid()).thenReturn(true);
        when(token3.isValid()).thenReturn(false);
        doNothing().when(token1).invalidate();
        doNothing().when(token2).invalidate();

        when(userAuthTokenRepository.getUserTokensWithRole(user, role)).thenReturn(List.of(token1, token2, token3));
        when(authTokenRepository.save(token1)).thenReturn(token1);
        when(authTokenRepository.save(token2)).thenReturn(token2);

        authTokenManager.removeAllUserTokensWithRole(event);

        verify(token1, times(1)).isValid();
        verify(token1, times(1)).invalidate();
        verify(token2, times(1)).isValid();
        verify(token2, times(1)).invalidate();
        verifyNoMoreInteractions(token1, token2);
        verify(token3, only()).isValid();
        verify(userAuthTokenRepository, times(1)).getUserTokensWithRole(user, role);
        verify(authTokenRepository, times(1)).save(token1);
        verify(authTokenRepository, times(1)).save(token2);
        verifyNoMoreInteractions(authTokenRepository);
        verifyZeroInteractions(userRepository, userCredentialRepository, passwordEncoder, tokenEncoder);
    }

    /**
     * Tests that handling a {@link UserEvent} by the {@link AuthTokenManager#removeAllUserTokens(UserEvent)} method
     * works as expected.
     *
     * @param event  A mocked {@link UserEvent} (the one being passed to the method).
     * @param user   A mocked {@link User} (the one contained in the event)
     * @param token1 A mocked {@link AuthToken} (one of the {@link AuthToken}s belonging to the {@code user}).
     * @param token2 A mocked {@link AuthToken} (one of the {@link AuthToken}s belonging to the {@code user}).
     * @param token3 A mocked {@link AuthToken} (one of the {@link AuthToken}s belonging to the {@code user}).
     */
    @Test
    void testRemoveAllUserTokens(
            @Mock(name = "event") final UserEvent event,
            @Mock(name = "user") final User user,
            @Mock(name = "token1") final UserAuthToken token1,
            @Mock(name = "token2") final UserAuthToken token2,
            @Mock(name = "token3") final UserAuthToken token3) {
        when(event.getUser()).thenReturn(user);
        when(token1.isValid()).thenReturn(true);
        when(token2.isValid()).thenReturn(true);
        when(token3.isValid()).thenReturn(false);
        doNothing().when(token1).invalidate();
        doNothing().when(token2).invalidate();

        when(userAuthTokenRepository.getUserTokens(user)).thenReturn(List.of(token1, token2, token3));
        when(authTokenRepository.save(token1)).thenReturn(token1);
        when(authTokenRepository.save(token2)).thenReturn(token2);

        authTokenManager.removeAllUserTokens(event);

        verify(token1, times(1)).isValid();
        verify(token1, times(1)).invalidate();
        verify(token2, times(1)).isValid();
        verify(token2, times(1)).invalidate();
        verifyNoMoreInteractions(token1, token2);
        verify(token3, only()).isValid();
        verify(userAuthTokenRepository, times(1)).getUserTokens(user);
        verify(authTokenRepository, times(1)).save(token1);
        verify(authTokenRepository, times(1)).save(token2);
        verifyNoMoreInteractions(authTokenRepository);
        verifyZeroInteractions(userRepository, userCredentialRepository, passwordEncoder, tokenEncoder);
    }


    // ================================================================================================================
    // Validation tests
    // ================================================================================================================

    /**
     * Tests that trying to issue an {@link AuthToken} for a deactivated {@link User}
     * throws an {@link UnauthenticatedException}.
     *
     * @param user A mocked {@link User} (the one for which a token is being tried to be issued).
     */
    @Test
    void testIssueTokenForNonActiveUser(@Mock(name = "user") final User user) {
        final var username = TestHelper.validUsername();
        final var inputPassword = TestHelper.validPassword();
        when(user.isActive()).thenReturn(false);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        Assertions.assertThrows(
                UnauthenticatedException.class,
                () -> authTokenManager.issueTokenForUser(username, inputPassword),
                "Issuing a token for a deactivated user is not failing"
        );
        verify(userRepository, only()).findByUsername(username);
        verifyZeroInteractions(userCredentialRepository, passwordEncoder, authTokenRepository, tokenEncoder);
    }

    /**
     * Tests that trying to issue an {@link AuthToken} for a {@link User} that does not contain {@link UserCredential}s
     * throws an {@link UnauthenticatedException}.
     *
     * @param user A mocked {@link User} (the one for which a token is being tried to be issued).
     */
    @Test
    void testIssueTokenWithUserWithoutCredential(@Mock(name = "user") final User user) {
        final var username = TestHelper.validUsername();
        final var inputPassword = TestHelper.validPassword();
        when(user.isActive()).thenReturn(true);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userCredentialRepository.findLastForUser(user)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                UnauthenticatedException.class,
                () -> authTokenManager.issueTokenForUser(username, inputPassword),
                "Issuing a token when there are no credentials for a user is not failing"
        );
        verify(userRepository, only()).findByUsername(username);
        verify(userCredentialRepository, only()).findLastForUser(user);
        verifyZeroInteractions(passwordEncoder, authTokenRepository, tokenEncoder);
    }

    /**
     * Tests that trying to issue an {@link AuthToken} when the used password does not match
     * throws an {@link UnauthenticatedException}.
     *
     * @param user           A mocked {@link User} (the one for which a token is being tried to be issued).
     * @param userCredential A mocked {@link UserCredential} (the one being matched).
     */
    @Test
    void testIssueTokenWithNonMatchingPassword(
            @Mock(name = "user") final User user,
            @Mock(name = "userCredential") final UserCredential userCredential) {
        final var username = TestHelper.validUsername();
        final var hashedPassword = TestHelper.validPassword();
        final var inputPassword = TestHelper.validPassword() + "another";
        when(user.isActive()).thenReturn(true);
        when(userCredential.getHashedPassword()).thenReturn(hashedPassword);
        when(passwordEncoder.matches(inputPassword, hashedPassword)).thenReturn(false);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userCredentialRepository.findLastForUser(user)).thenReturn(Optional.of(userCredential));
        Assertions.assertThrows(
                UnauthenticatedException.class,
                () -> authTokenManager.issueTokenForUser(username, inputPassword),
                "Issuing a token with an invalid password is not failing"
        );
        verify(userRepository, only()).findByUsername(username);
        verify(userCredentialRepository, only()).findLastForUser(user);
        verify(passwordEncoder, only()).matches(inputPassword, hashedPassword);
        verifyZeroInteractions(authTokenRepository, tokenEncoder);
    }

    /**
     * Tests that trying to refresh a blacklisted {@link AuthToken} throws an {@link UnauthenticatedException}.
     *
     * @param token A mocked {@link AuthToken} (the one being tried to be refreshed).
     */
    @Test
    void testRefreshTokenForBlacklistedToken(@Mock(name = "token") final AuthToken token) {
        final var tokenId = TestHelper.validTokenId();
        when(token.isValid()).thenReturn(false);
        when(authTokenRepository.findById(tokenId)).thenReturn(Optional.of(token));
        Assertions.assertThrows(
                UnauthorizedException.class,
                () -> authTokenManager.refreshToken(tokenId),
                "Issuing a token for a deactivated user is not failing"
        );
        verify(token, only()).isValid();
        verify(authTokenRepository, only()).findById(tokenId);
        verifyZeroInteractions(userRepository, userCredentialRepository, passwordEncoder, tokenEncoder);
    }

    /**
     * Tests that trying to refresh an {@link AuthToken} that belongs to a deactivated {@link User}
     * throws an {@link UnauthenticatedException}.
     *
     * @param token A mocked {@link AuthToken} (the one being tried to be refreshed).
     */
    @Test
    void testRefreshTokenForNonActiveUser(@Mock(name = "token", answer = RETURNS_DEEP_STUBS) final UserAuthToken token) {
        final var tokenId = TestHelper.validTokenId();
        when(token.isValid()).thenReturn(true);
        when(token.getUser().isActive()).thenReturn(false);
        when(authTokenRepository.findById(tokenId)).thenReturn(Optional.of(token));
        Assertions.assertThrows(
                UnauthorizedException.class,
                () -> authTokenManager.refreshToken(tokenId),
                "Issuing a token for a deactivated user is not failing"
        );
        verify(token, times(1)).isValid();
        verify(authTokenRepository, only()).findById(tokenId);
        verifyZeroInteractions(userRepository, userCredentialRepository, passwordEncoder, tokenEncoder);
    }


    // ================================================================================================================
    // Non existence tests
    // ================================================================================================================

    /**
     * Tests that trying to issue an {@link AuthToken}for a {@link User} that does not exist throws an
     * {@link UnauthenticatedException}.
     */
    @Test
    void testIssueTokenForNonExistenceUserThrowsUnauthenticatedException() {
        final var username = TestHelper.validUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                UnauthenticatedException.class,
                () -> authTokenManager.issueTokenForUser(username, TestHelper.validPassword()),
                "Issuing a token for a non existence user is not failing"
        );
        verifyOnlyUserSearch(username);
    }

    /**
     * Tests that trying to refresh an {@link AuthToken} that does not exist throws an {@link UnauthorizedException}.
     */
    @Test
    void testRefreshTokenForNonExistenceTokenThrowsUnauthorizedException() {
        final var tokenId = TestHelper.validTokenId();
        when(authTokenRepository.findById(tokenId)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                UnauthorizedException.class,
                () -> authTokenManager.refreshToken(tokenId),
                "Refreshing a non existence token is not failing"
        );
        verifyOnlyTokenSearch(tokenId);
    }

    /**
     * Tests that trying to blacklist an {@link AuthToken} that does no exist does not fail.
     */
    @Test
    void testBlacklistTokenForNonExistenceTokenDoesNotThrowException() {
        final var tokenId = TestHelper.validTokenId();
        when(authTokenRepository.findById(tokenId)).thenReturn(Optional.empty());
        authTokenManager.blacklistToken(tokenId);
        verifyOnlyTokenSearch(tokenId);
    }

    /**
     * Tests that trying to list the {@link AuthToken}s of a {@link User} that does not exist
     * throws a {@link NoSuchEntityException}.
     */
    @Test
    void testListTokensForNonExistenceUserThrowsNoSuchEntityException() {
        final var username = TestHelper.validUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Assertions.assertThrows(
                NoSuchEntityException.class,
                () -> authTokenManager.listUserTokens(username),
                "Retrieving all tokens for a non existence user is not failing"
        );
        verifyOnlyUserSearch(username);
    }


    // ================================================================================================================
    // Illegal Argument tests
    // ================================================================================================================

    /**
     * Tests that passing {@code null} to {@link AuthTokenManager#removeAllUserTokensWithRole(UserRoleRemovedEvent)}
     * throws an {@link IllegalArgumentException}.
     */
    @Test
    void testRemoveAllUserTokensWithRoleWithNullEvent() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> authTokenManager.removeAllUserTokensWithRole(null),
                "Passing a null event to the remove all user tokens with role method is being allowed"
        );
        verifyNoInteractionsWithMocks();
    }

    /**
     * Tests that the {@link AuthTokenManager#removeAllUserTokensWithRole(UserRoleRemovedEvent)} method throws
     * an {@link IllegalArgumentException} when the event contains a {@code null} {@link User}.
     *
     * @param event A mocked {@link UserRoleRemovedEvent} (the one being passed to the method).
     */
    @Test
    void testRemoveAllUserTokensWithRoleWithEventWithNullUser(@Mock(name = "event") final UserRoleRemovedEvent event) {
        when(event.getUser()).thenReturn(null);
        when(event.getRole()).thenReturn(TestHelper.randomRole());
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> authTokenManager.removeAllUserTokensWithRole(event),
                "Passing an event with a null user to the remove all user tokens with role method is being allowed"
        );
        verifyNoInteractionsWithMocks();
    }

    /**
     * Tests that the {@link AuthTokenManager#removeAllUserTokensWithRole(UserRoleRemovedEvent)} method throws
     * an {@link IllegalArgumentException} when the event contains a {@code null} {@link Role}.
     *
     * @param event A mocked {@link UserRoleRemovedEvent} (the one being passed to the method).
     * @param user  A mocked {@link User}
     *              (the one contained in the event, included to avoid failure due to the lack of a {@link User})
     */
    @Test
    void testRemoveAllUserTokensWithRoleWithEventWithNullRole(
            @Mock(name = "event") final UserRoleRemovedEvent event,
            @Mock(name = "user") final User user) {
        when(event.getUser()).thenReturn(user);
        when(event.getRole()).thenReturn(null);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> authTokenManager.removeAllUserTokensWithRole(event),
                "Passing an event with a null user to the remove all user tokens with role method is being allowed"
        );
        verifyNoInteractionsWithMocks();
    }

    /**
     * Tests that passing {@code null} to {@link AuthTokenManager#removeAllUserTokens(UserEvent)}
     * throws an {@link IllegalArgumentException}.
     */
    @Test
    void testRemoveAllUserTokensWithNullEvent() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> authTokenManager.removeAllUserTokens(null),
                "Passing a null event to the remove all user tokens method is being allowed"
        );
        verifyNoInteractionsWithMocks();
    }

    /**
     * Tests that the {@link AuthTokenManager#removeAllUserTokens(UserEvent)}  method throws
     * an {@link IllegalArgumentException}  when the event contains a {@code null} {@link User}.
     *
     * @param event A mocked {@link UserEvent} (the one being passed to the method).
     */
    @Test
    void testRemoveAllUserTokensWithEventWithNullUser(@Mock(name = "event") final UserEvent event) {
        when(event.getUser()).thenReturn(null);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> authTokenManager.removeAllUserTokens(event),
                "Passing an event with a null user to the remove all user tokens method is being allowed"
        );
        verifyNoInteractionsWithMocks();
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Creates an {@link ArgumentMatcher} of {@link UserAuthToken} that checks if an {@link UserAuthToken}
     * belongs to the given {@code user}, and contains the given {@code roles}.
     *
     * @param user  The {@link User} to check.
     * @param roles The {@link Set} of {@link Role}s to check.
     * @return The created {@link ArgumentMatcher}.
     */
    private static ArgumentMatcher<UserAuthToken> matchingUserToken(final User user, final Set<Role> roles) {
        return (final UserAuthToken t) -> t.getUser().equals(user) && t.getRolesAssigned().equals(roles);
    }

    /**
     * Creates an {@link ArgumentMatcher} of {@link SubjectAuthToken} that checks if an {@link SubjectAuthToken}
     * belongs to the given {@code subject}, and contains the given {@code roles}.
     *
     * @param subject The subject to check.
     * @param roles   The {@link Set} of {@link Role}s to check.
     * @return The created {@link ArgumentMatcher}.
     */
    private static ArgumentMatcher<SubjectAuthToken> matchingSubjectToken(final String subject, final Set<Role> roles) {
        return (final SubjectAuthToken t) -> t.getSubject().equals(subject) && t.getRolesAssigned().equals(roles);
    }

    /**
     * Convenient method to verify that interactions with mocks only implies searching for a {@link User}
     * using the {@link UserRepository}.
     *
     * @param username The username used to search.
     */
    private void verifyOnlyUserSearch(final String username) {
        verify(userRepository, only()).findByUsername(username);
        verifyZeroInteractions(
                userCredentialRepository,
                authTokenRepository,
                passwordEncoder,
                tokenEncoder
        );
    }

    /**
     * Convenient method to verify that interactions with mocks only implies searching for an {@link AuthToken}
     * using the {@link UserCredentialRepository}.
     *
     * @param tokenId The id used to search.
     */
    private void verifyOnlyTokenSearch(final UUID tokenId) {
        verify(authTokenRepository, only()).findById(tokenId);
        verifyZeroInteractions(
                userRepository,
                userCredentialRepository,
                passwordEncoder,
                tokenEncoder
        );
    }

    /**
     * Convenient method to verify there is no interaction with any mock.
     */
    private void verifyNoInteractionsWithMocks() {
        verifyZeroInteractions(
                userRepository,
                userCredentialRepository,
                authTokenRepository,
                passwordEncoder,
                tokenEncoder
        );
    }
}
