package com.example.cinema.api.application.dto.payment;


import com.example.cinema.api.domain.payment.exception.PaymentValidationException;

public record PaymentAddressDTO(String zipCode, String streetName, String streetNumber,
                                String neighborhood,
                                String city,
                                String federalUnit) {
    public PaymentAddressDTO {
        if (zipCode == null || zipCode.isBlank()) throw new PaymentValidationException("CEP é obrigatório");
        if (streetName == null || streetName.isBlank()) throw new PaymentValidationException("Logradouro é obrigatório");
        if (streetNumber == null || streetNumber.isBlank()) throw new PaymentValidationException("Número é obrigatório");
        if (city == null || city.isBlank()) throw new PaymentValidationException("Cidade é obrigatória");
        if (federalUnit == null || federalUnit.isBlank()) throw new PaymentValidationException("Estado é obrigatório");
    }
}