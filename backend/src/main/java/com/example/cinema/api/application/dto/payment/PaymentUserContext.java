package com.example.cinema.api.application.dto.payment;

import com.example.cinema.api.domain.payment.exception.PaymentValidationException;
import com.example.cinema.api.domain.user.User;

public record PaymentUserContext(String email, String cpf, String name) {

    public PaymentUserContext {
        if (email == null || email.isBlank()) throw new PaymentValidationException("Email do pagador é obrigatório");
        if (cpf == null || cpf.isBlank()) throw new PaymentValidationException("CPF do pagador é obrigatório");
        if (name == null || name.isBlank()) throw new PaymentValidationException("Nome do pagador é obrigatório");
    }

    public static PaymentUserContext from(User user) {
        return new PaymentUserContext(
                user.getEmail(),
                user.getCpf(),
                user.getName()
        );
    }
}