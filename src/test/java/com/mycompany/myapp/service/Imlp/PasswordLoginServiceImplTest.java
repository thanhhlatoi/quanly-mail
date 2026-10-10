package com.mycompany.myapp.service.Imlp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.myapp.domain.Role;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.dto.request.LoginRequest;
import com.mycompany.myapp.dto.response.LoginResponse;
import com.mycompany.myapp.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class PasswordLoginServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private Jwt jwt;

    private PasswordLoginServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PasswordLoginServiceImpl(userRepository, passwordEncoder, jwtEncoder);
    }

    @Test
    void shouldLoginWithValidCredentials() {
        User user = user(7L, "thanh", "thanh@example.com", "hashed-password", "ADMIN");
        when(userRepository.findByUserNameIgnoreCase("thanh")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "hashed-password")).thenReturn(true);
        when(jwtEncoder.encode(any())).thenReturn(jwt);
        when(jwt.getTokenValue()).thenReturn("jwt-token");

        LoginResponse response = service.login(new LoginRequest(" thanh ", "secret"));

        assertThat(response.getIdToken()).isEqualTo("jwt-token");
        assertThat(response.getUserId()).isEqualTo(7L);
        assertThat(response.getEmail()).isEqualTo("thanh@example.com");
        assertThat(response.getAuthorities()).containsExactly("ROLE_ADMIN");
    }

    @Test
    void shouldRejectInvalidPassword() {
        User user = user(7L, "thanh", "thanh@example.com", "hashed-password", "USER");
        when(userRepository.findByUserNameIgnoreCase("thanh")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed-password")).thenReturn(false);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.login(new LoginRequest("thanh", "wrong"))
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(401);
        verify(jwtEncoder, never()).encode(any());
    }

    @Test
    void shouldRejectInactiveUser() {
        User user = user(7L, "thanh", "thanh@example.com", "hashed-password", "USER");
        user.setStatus(false);
        when(userRepository.findByUserNameIgnoreCase("thanh")).thenReturn(Optional.of(user));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.login(new LoginRequest("thanh", "secret"))
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(401);
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void shouldRejectGoogleOnlyAccount() {
        User user = user(7L, "thanh@example.com", "thanh@example.com", "GOOGLE_LOGIN_ONLY", "USER");
        when(userRepository.findByUserNameIgnoreCase("thanh@example.com")).thenReturn(Optional.of(user));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.login(new LoginRequest("thanh@example.com", "secret"))
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(401);
        verify(passwordEncoder, never()).matches(any(), any());
    }

    private User user(Long id, String userName, String email, String password, String roleName) {
        User user = new User();
        user.setId(id);
        user.setUserName(userName);
        user.setEmail(email);
        user.setPassword(password);
        user.setStatus(true);
        user.getRoles().add(new Role(null, roleName, null, null, null));
        return user;
    }
}
