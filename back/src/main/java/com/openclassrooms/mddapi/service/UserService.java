package com.openclassrooms.mddapi.service;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.RegisterRequest;
import com.openclassrooms.mddapi.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.dto.UserDto;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;

/**
 * Implémentation de {@link IUserService} et {@link UserDetailsService}.
 * Gère l'inscription, la mise à jour du profil et l'authentification Spring Security.
 */
@Service
public class UserService implements IUserService, UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Spring Security — identifiant = email ou username
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + identifier));
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Email déjà utilisé");
        if (userRepository.existsByUsername(request.getUsername()))
            throw new IllegalArgumentException("Nom d'utilisateur déjà utilisé");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        User saved = userRepository.save(user);
        log.info("Nouvel utilisateur enregistré : id={}", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public UserDto updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (request.getUsername() != null && !request.getUsername().equals(user.getRealUsername())) {
            if (userRepository.existsByUsername(request.getUsername()))
                throw new IllegalArgumentException("Nom d'utilisateur déjà utilisé");
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail()))
                throw new IllegalArgumentException("Email déjà utilisé");
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(request.getPassword()));

        UserDto dto = toDto(userRepository.save(user));
        log.info("Profil mis à jour : id={}", user.getId());
        return dto;
    }

    @Override
    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getRealUsername(), user.getEmail());
    }
}
