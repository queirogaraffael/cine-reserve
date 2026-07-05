package com.example.cinema.api.domain.confirmacao;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "confirmacoes_cadastro")
public class ConfirmacaoCadastro {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(nullable = false, length = 6)
    private String codigo;

    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime dataExpiracao;

    @Column(nullable = false)
    private boolean utilizado = false;

    public ConfirmacaoCadastro(UUID usuarioId, String codigo, LocalDateTime dataExpiracao) {
        this.usuarioId = usuarioId;
        this.codigo = codigo;
        this.dataExpiracao = dataExpiracao;
        this.utilizado = false;
    }

    public boolean isExpirada() {
        return LocalDateTime.now().isAfter(this.dataExpiracao);
    }

    public void marcarComoUtilizado() {
        this.utilizado = true;
    }
}
