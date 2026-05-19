package com.openclassrooms.mddapi.config;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;

@Component
@Profile({"!test", "!prod"})
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.init.user.password:Test1234!}")
    private String initUserPassword;

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
            user.setPassword(passwordEncoder.encode(initUserPassword));
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
            log.info("Utilisateur de test créé : user@mdd.com");
        }
    }
}
