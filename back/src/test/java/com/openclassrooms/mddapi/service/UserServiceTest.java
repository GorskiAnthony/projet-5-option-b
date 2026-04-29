package com.openclassrooms.mddapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.openclassrooms.mddapi.dto.RegisterRequest;
import com.openclassrooms.mddapi.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.dto.UserDto;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService — tests unitaires")
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword("encoded-password");
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("register — doit persister l'utilisateur quand email et username sont disponibles")
    void register_shouldPersistUser_whenCredentialsAreAvailable() {
        // Arrange
        RegisterRequest req = new RegisterRequest();
        req.setUsername("alice");
        req.setEmail("alice@example.com");
        req.setPassword("secret123");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.register(req);

        // Assert
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register — doit lever une exception quand l'email est déjà utilisé")
    void register_shouldThrow_whenEmailAlreadyExists() {
        // Arrange
        RegisterRequest req = new RegisterRequest();
        req.setEmail("alice@example.com");
        req.setUsername("alice");
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email déjà utilisé");
    }

    @Test
    @DisplayName("register — doit lever une exception quand le username est déjà utilisé")
    void register_shouldThrow_whenUsernameAlreadyExists() {
        // Arrange
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@example.com");
        req.setUsername("alice");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Nom d'utilisateur déjà utilisé");
    }

    @Test
    @DisplayName("updateProfile — doit mettre à jour username et email")
    void updateProfile_shouldUpdateUsernameAndEmail() {
        // Arrange
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setUsername("alice2");
        req.setEmail("alice2@example.com");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserDto dto = userService.updateProfile("alice@example.com", req);

        // Assert
        assertThat(dto).isNotNull();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("updateProfile — doit encoder le mot de passe si fourni")
    void updateProfile_shouldEncodePassword_whenPasswordProvided() {
        // Arrange
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setPassword("newSecret123");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newSecret123")).thenReturn("new-encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.updateProfile("alice@example.com", req);

        // Assert
        verify(passwordEncoder).encode("newSecret123");
    }

    @Test
    @DisplayName("loadUserByUsername — doit retourner l'utilisateur quand l'email existe")
    void loadUserByUsername_shouldReturnUser_whenEmailExists() {
        // Arrange
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThat(userService.loadUserByUsername("alice@example.com")).isEqualTo(user);
    }

    @Test
    @DisplayName("loadUserByUsername — doit lever UsernameNotFoundException si l'identifiant est introuvable")
    void loadUserByUsername_shouldThrow_whenNotFound() {
        // Arrange
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("unknown@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.loadUserByUsername("unknown@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
