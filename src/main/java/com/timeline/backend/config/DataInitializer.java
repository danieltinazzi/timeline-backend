package com.timeline.backend.config;

import com.timeline.backend.user.User;
import com.timeline.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:#{null}}")
    private String adminUsername;

    @Value("${app.admin.password:#{null}}")
    private String adminPassword;

    public DataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (adminUsername != null &&
            adminPassword != null &&
            !userRepository.existsByUsername(adminUsername)
        ) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEnabled(true);

            userRepository.save(admin);
            System.out.println("Admin user created: " + adminUsername);
        }
    }
}
