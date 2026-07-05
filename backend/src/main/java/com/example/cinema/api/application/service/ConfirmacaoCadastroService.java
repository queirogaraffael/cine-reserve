package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.confirmacao.ConfirmacaoCadastro;
import com.example.cinema.api.domain.confirmacao.event.EmailVerificacaoEvent;
import com.example.cinema.api.domain.confirmacao.exception.CodigoInvalidoException;
import com.example.cinema.api.domain.confirmacao.exception.ConfirmacaoExpiradaException;
import com.example.cinema.api.domain.confirmacao.exception.ConfirmacaoNotFoundException;
import com.example.cinema.api.domain.confirmacao.exception.EmailJaConfirmadoException;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.user.exception.UserNotFoundException;
import com.example.cinema.api.infrastructure.persistence.ConfirmacaoCadastroRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConfirmacaoCadastroService {

    private final ConfirmacaoCadastroRepositoryJpa confirmacaoRepository;
    private final UserRepositoryJpa userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final SecureRandom secureRandom = new SecureRandom();

    public ConfirmacaoCadastroService(ConfirmacaoCadastroRepositoryJpa confirmacaoRepository, UserRepositoryJpa userRepository, ApplicationEventPublisher eventPublisher) {
        this.confirmacaoRepository = confirmacaoRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public String gerarCodigo(UUID usuarioId) {
        confirmacaoRepository.deleteByUsuarioId(usuarioId);

        int numero = secureRandom.nextInt(1000000);
        String codigo = String.format("%06d", numero);

        ConfirmacaoCadastro confirmacao = new ConfirmacaoCadastro(usuarioId, codigo, LocalDateTime.now().plusMinutes(15));
        confirmacaoRepository.save(confirmacao);

        return codigo;
    }

    @Transactional
    public void confirmarEmail(UUID usuarioId, String codigo) {
        ConfirmacaoCadastro confirmacao = confirmacaoRepository.findByUsuarioIdAndUtilizadoFalse(usuarioId)
                .orElseThrow(() -> new ConfirmacaoNotFoundException("Confirmação não encontrada para o usuário."));

        if (confirmacao.isExpirada()) {
            throw new ConfirmacaoExpiradaException("O código de verificação expirou.");
        }

        if (!confirmacao.getCodigo().equals(codigo)) {
            throw new CodigoInvalidoException("O código de verificação é inválido.");
        }

        confirmacao.marcarComoUtilizado();
        confirmacaoRepository.save(confirmacao);

        User user = userRepository.findById(usuarioId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));

        user.marcarEmailComoConfirmado();
        userRepository.save(user);
    }

    @Transactional
    public void reenviarCodigo(UUID usuarioId) {
        User user = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (user.isEmailConfirmado()) {
            throw new EmailJaConfirmadoException("O e-mail deste usuário já foi confirmado.");
        }

        String codigo = gerarCodigo(usuarioId);

        eventPublisher.publishEvent(new EmailVerificacaoEvent(user.getId(), user.getEmail(), user.getName(), codigo));
    }
}
