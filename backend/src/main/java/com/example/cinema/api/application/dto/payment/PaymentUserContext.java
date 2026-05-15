package com.example.cinema.api.application.dto.payment;

public record PaymentUserContext(
        String email,
        String cpf,
        String name
) {
    public PaymentUserContext {
        if (email == null || email.isBlank()) throw new ValidationException("Email do pagador é obrigatório");
        if (cpf == null || cpf.isBlank()) throw new ValidationException("CPF do pagador é obrigatório");
        if (name == null || name.isBlank()) throw new ValidationException("Nome do pagador é obrigatório");
    }
}