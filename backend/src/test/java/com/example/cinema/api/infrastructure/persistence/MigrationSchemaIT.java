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

    static final boolean DOCKER_AVAILABLE;
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("cinema_migration_test")
            .withUsername("test")
            .withPassword("test");

    static {
        boolean available = false;
        try {
            available = org.testcontainers.DockerClientFactory.instance().isDockerAvailable();
        } catch (Exception e) {}
        DOCKER_AVAILABLE = available;
        if (DOCKER_AVAILABLE) {
            POSTGRES.start();
        }
    }

    @org.junit.jupiter.api.BeforeAll
    static void checkDocker() {
        org.junit.jupiter.api.Assumptions.assumeTrue(DOCKER_AVAILABLE, "Docker is not available. Skipping MigrationSchemaIT.");
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        if (!DOCKER_AVAILABLE) return;
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");

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
            "cinema",
            "movie_exhibition",
            "rooms",
            "seats",
            "movie_session",
            "users",
            "registration_confirmations",
            "orders",
            "order_items",
            "order_payment",
            "payment_transaction",
            "ticket_types",
            "promotions",
            "seat_reservations",
            "coupons");

    private static final List<String> EXPECTED_INDEXES = List.of(
            "idx_movie_genre_id",
            "idx_movie_release_date",
            "idx_room_name_cinema",
            "idx_seat_room",
            "idx_movie_exhibition_movie_id",
            "idx_movie_exhibition_cinema_id",
            "idx_movie_session_exhibition_id",
            "idx_movie_session_room_id",
            "idx_movie_session_show_date",
            "idx_movie_session_available",
            "idx_users_is_locked",
            "idx_registration_user_id",
            "idx_order_user_id",
            "idx_order_status",
            "idx_order_item_order_id",
            "idx_order_item_ticket_type_id",
            "idx_order_payment_provider_payment_id",
            "idx_order_payment_status",
            "idx_seat_reservation_session_id",
            "idx_seat_reservation_order_id",
            "idx_seat_reservation_active",
            "idx_coupon_user_id",
            "idx_coupon_code",
            "idx_coupon_status",
            "idx_coupon_cinema_id");

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