package com.example.cinema.api.infrastructure.persistence;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class MigrationSchemaIT {

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("cinema_migration_test")
            .withUsername("test")
            .withPassword("test");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.flyway.enabled", () -> "true");

        registry.add("spring.mail.host", () -> "localhost");

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");

        registry.add("spring.cache.type", () -> "none");
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> "6370");
    }

    @Autowired
    private DataSource dataSource;

    private static final List<String> EXPECTED_TABLES = List.of(
            "genre",
            "movie",
            "room",
            "movie_session",
            "users",
            "confirmacoes_cadastro",
            "purchase",
            "payment",
            "tickets",
            "seat_reservations");

    private static final List<String> EXPECTED_INDEXES = List.of(
            "idx_movie_genre_id",
            "idx_movie_release_date",
            "idx_movie_session_movie_id",
            "idx_movie_session_room_id",
            "idx_movie_session_show_date",
            "idx_movie_session_available",
            "idx_users_is_locked",
            "idx_confirmacao_usuario_id",
            "idx_purchase_user_id",
            "idx_purchase_status",
            "idx_purchase_user_status",
            "idx_payment_transaction_id",
            "idx_payment_status",
            "idx_ticket_purchase_id",
            "idx_ticket_session_id",
            "idx_seat_reservation_session_id",
            "idx_seat_reservation_user_id",
            "idx_seat_reservation_active");

    @Test
    void migrationV1_deveExecutarSemErros() {
        assertThatCode(() -> Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load()
                .info()).doesNotThrowAnyException();
    }

    @Test
    void migrationV1_deveCriarTodasAsTabelas() throws Exception {
        List<String> tablesFound = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(null, "public", "%", new String[] { "TABLE" })) {
                while (rs.next()) {
                    tablesFound.add(rs.getString("TABLE_NAME").toLowerCase());
                }
            }
        }

        assertThat(tablesFound)
                .as("Tabelas criadas pela migration V1")
                .containsAll(EXPECTED_TABLES);
    }

    @Test
    void migrationV1_deveCriarTodosOsIndexes() throws Exception {
        List<String> indexesFound = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            for (String table : EXPECTED_TABLES) {
                try (ResultSet rs = meta.getIndexInfo(null, "public", table, false, false)) {
                    while (rs.next()) {
                        String indexName = rs.getString("INDEX_NAME");
                        if (indexName != null) {
                            indexesFound.add(indexName.toLowerCase());
                        }
                    }
                }
            }
        }

        assertThat(indexesFound)
                .as("Indexes criados pela migration V1")
                .containsAll(EXPECTED_INDEXES);
    }
}