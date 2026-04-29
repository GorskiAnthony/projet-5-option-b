package com.openclassrooms.mddapi.service;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.RegisterRequest;
import com.openclassrooms.mddapi.dto.UpdateProfileRequest;
import com.openclassrooms.mddapi.dto.UserDto;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;

@Service
public class UserService implements IUserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Spring Security — identifiant = email
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .or(() -> userRepository.findByUsername(email))
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email déjà utilisé");
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Nom d'utilisateur déjà utilisé");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDto updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        return toDto(userRepository.save(user));
    }

    @Override
    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getRealUsername(), user.getEmail());
    }
}
