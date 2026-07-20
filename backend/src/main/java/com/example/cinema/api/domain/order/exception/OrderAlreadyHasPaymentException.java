package com.example.cinema.api.domain.order.exception;
public class OrderAlreadyHasPaymentException extends RuntimeException {
    public OrderAlreadyHasPaymentException(String message) { super(message); }
}
