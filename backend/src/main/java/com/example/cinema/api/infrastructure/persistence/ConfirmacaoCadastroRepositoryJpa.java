package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.confirmacao.ConfirmacaoCadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfirmacaoCadastroRepositoryJpa extends JpaRepository<ConfirmacaoCadastro, UUID> {

    Optional<ConfirmacaoCadastro> findByUsuarioIdAndUtilizadoFalse(UUID usuarioId);

    @Modifying
    @Transactional
    void deleteByUsuarioId(UUID usuarioId);
}
