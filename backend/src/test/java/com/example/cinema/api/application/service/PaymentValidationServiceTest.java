package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.order.Order;
import com.example.cinema.api.domain.order.OrderStatus;
import com.example.cinema.api.domain.payment.OrderPayment;
import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.domain.payment.exception.OrderAlreadyPaidException;
import com.example.cinema.api.domain.movie.MovieSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PaymentValidationServiceTest {

    private PaymentValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new PaymentValidationService();
        ReflectionTestUtils.setField(validationService, "maxRetryAttempts", 5);
        ReflectionTestUtils.setField(validationService, "minRetryIntervalSeconds", 30);
    }

    @Test
    void isEligibleForRetry_ReturnsTrue_WhenValid() {
        Order order = Mockito.mock(Order.class);
        when(order.getStatus()).thenReturn(OrderStatus.WAITING_PAYMENT);
        MovieSession session = Mockito.mock(MovieSession.class);
        when(order.getMovieSession()).thenReturn(session);

        OrderPayment payment = Mockito.mock(OrderPayment.class);
        when(payment.getPaymentStatus()).thenReturn(PaymentStatus.REJECTED);
        when(payment.getPaymentDate()).thenReturn(LocalDateTime.now().minusMinutes(1));

        assertTrue(validationService.isEligibleForRetry(order, payment, 1));
    }

    @Test
    void validateRetry_ThrowsException_WhenOrderPaid() {
        Order order = Mockito.mock(Order.class);
        when(order.getStatus()).thenReturn(OrderStatus.WAITING_PAYMENT);
        
        OrderPayment payment = Mockito.mock(OrderPayment.class);
        when(payment.getPaymentStatus()).thenReturn(PaymentStatus.APPROVED);

        assertThrows(OrderAlreadyPaidException.class, () -> 
            validationService.validateRetry(order, payment, 1)
        );
    }
}
