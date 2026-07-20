package com.example.cinema.api.domain.order.exception;
public class InvalidPaymentAmountException extends RuntimeException {
    public InvalidPaymentAmountException(String message) { super(message); }
}
