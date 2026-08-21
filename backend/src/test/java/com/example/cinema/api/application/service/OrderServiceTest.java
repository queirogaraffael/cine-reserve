package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.order.OrderSeatSelectionRequestDTO;
import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.movie.exception.MovieSessionNotAvailableForPurchaseException;
import com.example.cinema.api.domain.order.Order;
import com.example.cinema.api.infrastructure.persistence.OrderRepositoryJpa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepositoryJpa orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void selectSeats_ThrowsException_WhenSessionNotAvailableForReservation() {
        Long orderId = 1L;
        UUID userId = UUID.randomUUID();
        Order order = mock(Order.class);
        MovieSession session = mock(MovieSession.class);
        
        when(orderRepository.findByIdAndUserId(orderId, userId)).thenReturn(Optional.of(order));
        when(order.getMovieSession()).thenReturn(session);
        Mockito.doThrow(new MovieSessionNotAvailableForPurchaseException("Sessão não está disponível para compra."))
                .when(session).validateAvailabilityForReservation();

        OrderSeatSelectionRequestDTO dto = new OrderSeatSelectionRequestDTO();

        assertThrows(MovieSessionNotAvailableForPurchaseException.class, () -> {
            orderService.selectSeats(orderId, dto, userId);
        });
    }
}
