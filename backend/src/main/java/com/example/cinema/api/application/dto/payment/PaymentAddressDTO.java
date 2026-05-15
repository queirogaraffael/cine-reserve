package com.example.cinema.api.application.dto.payment;

public record PaymentAddressDTO(
        String zipCode,
        String streetName,
        String streetNumber,
        String neighborhood,
        String city,
        String federalUnit
) {
    public PaymentAddressDTO {
        if (zipCode == null || zipCode.isBlank()) throw new ValidationException("CEP é obrigatório");
        if (streetName == null || streetName.isBlank()) throw new ValidationException("Logradouro é obrigatório");
        if (streetNumber == null || streetNumber.isBlank()) throw new ValidationException("Número é obrigatório");
        if (city == null || city.isBlank()) throw new ValidationException("Cidade é obrigatória");
        if (federalUnit == null || federalUnit.isBlank()) throw new ValidationException("Estado é obrigatório");
    }
}