package com.openclassrooms.mddapi.config;

import java.time.LocalDateTime;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;

@Component
@Profile("!test")
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByEmail("user@mdd.com")) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@mdd.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
            System.out.println("✓ Utilisateur de test créé : user@mdd.com / password");
        }
    }
}
