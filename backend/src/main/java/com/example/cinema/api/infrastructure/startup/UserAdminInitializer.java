package com.example.cinema.api.infrastructure.startup;

import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.user.UserRole;
import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.CinemaRepositoryJpa;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@Profile("dev")
@Order(2)
public class UserAdminInitializer implements CommandLineRunner {

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.nome}")
    private String adminNome;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.cpf}")
    private String adminCpf;

    @Value("${app.cinema-admin.password:123456}")
    private String cinemaAdminPassword;

    @Value("${app.cinema-admin.nome:Cinema Admin DEV}")
    private String cinemaAdminNome;

    @Value("${app.cinema-admin.email:cinema_admin@dev.local}")
    private String cinemaAdminEmail;

    @Value("${app.cinema-admin.cpf:11111111111}")
    private String cinemaAdminCpf;

    private final UserRepositoryJpa userRepositoryJpa;
    private final CinemaRepositoryJpa cinemaRepositoryJpa;
    private final PasswordEncoder passwordEncoder;

    public UserAdminInitializer(UserRepositoryJpa userRepositoryJpa, CinemaRepositoryJpa cinemaRepositoryJpa, PasswordEncoder passwordEncoder) {
        this.userRepositoryJpa = userRepositoryJpa;
        this.cinemaRepositoryJpa = cinemaRepositoryJpa;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        if (!userRepositoryJpa.existsByEmail(adminEmail)) {
            User admin = new User(
                    adminCpf,
                    adminNome,
                    adminEmail,
                    passwordEncoder.encode(adminPassword),
                    "11988888888",
                    null,
                    null,
                    true,
                    LocalDate.now(),
                    LocalDate.of(1990, 1, 1),
                    UserRole.SUPER_ADMIN);
            userRepositoryJpa.save(admin);
            System.out.println("Usuário admin (" + adminEmail + ") criado!");
        }

        if (!userRepositoryJpa.existsByEmail(cinemaAdminEmail)) {
            Optional<Cinema> cinemaOpt = cinemaRepositoryJpa.findAll().stream().findFirst();
            if (cinemaOpt.isPresent()) {
                User cinemaAdmin = new User(
                        cinemaAdminCpf,
                        cinemaAdminNome,
                        cinemaAdminEmail,
                        passwordEncoder.encode(cinemaAdminPassword),
                        "11977777777",
                        null,
                        null,
                        true,
                        LocalDate.now(),
                        LocalDate.of(1992, 1, 1),
                        UserRole.CINEMA_ADMIN);
                cinemaAdmin.setCinema(cinemaOpt.get());
                userRepositoryJpa.save(cinemaAdmin);
                System.out.println("Usuário cinema_admin (" + cinemaAdminEmail + ") criado para o cinema: " + cinemaOpt.get().getName());
            } else {
                System.out.println("Nenhum cinema encontrado para vincular ao cinema_admin!");
            }
        }

    }
}
