package com.example.cinema.api.application.initialization;

import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.user.UserRole;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserAdminInitializer implements CommandLineRunner {

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.nome}")
    private String adminNome;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.cpf}")
    private String adminCpf;

    private final UserRepositoryJpa userRepositoryJpa;
    private final PasswordEncoder passwordEncoder;

    public UserAdminInitializer(UserRepositoryJpa userRepositoryJpa, PasswordEncoder passwordEncoder) {
        this.userRepositoryJpa = userRepositoryJpa;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        if (!userRepositoryJpa.existsByUsername(adminUsername)) {
            User admin = new User(
                    adminUsername,
                    adminCpf,
                    adminNome,
                    adminEmail,
                    passwordEncoder.encode(adminPassword),
                    LocalDate.now(),
                    LocalDate.of(1990, 1, 1),
                    UserRole.ADMIN);
            userRepositoryJpa.save(admin);
            System.out.println("Usuário admin (" + adminUsername + ") criado!");
        }

    }
}
