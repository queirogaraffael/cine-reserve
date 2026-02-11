package com.example.cinema.api.domain.service;

import com.example.cinema.api.domain.entities.*;
import com.example.cinema.api.domain.enums.ReservationStatus;
import com.example.cinema.api.domain.pricing.context.TicketPricingContext;
import com.example.cinema.api.domain.purchase.event.PurchaseCreatedEvent;
import com.example.cinema.api.infrastructure.persistence.PurchaseRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.SeatReservationRepositoryJpa;
import com.example.cinema.api.shared.dtos.email.PurchaseCreatedNotificationData;
import com.example.cinema.api.shared.dtos.purchase.PurchaseResponseDTO;
import com.example.cinema.api.shared.dtos.purchase.TicketItemDTO;
import com.example.cinema.api.shared.dtos.purchase.TicketPurchaseRequestDTO;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import com.example.cinema.api.shared.mappers.PurchaseMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PurchaseService {

    private final PurchaseRepositoryJpa purchaseRepository;
    private final SeatReservationRepositoryJpa seatReservationRepositoryJpa;
    private final PurchaseMapper purchaseMapper;
    private final UserService userService;
    private final TicketPricingContext ticketPricingContext;
    private final ApplicationEventPublisher eventPublisher;

    public PurchaseService(PurchaseRepositoryJpa purchaseRepository, SeatReservationRepositoryJpa seatReservationRepositoryJpa, PurchaseMapper purchaseMapper, UserService userService, TicketPricingContext ticketPricingContext, ApplicationEventPublisher eventPublisher) {
        this.purchaseRepository = purchaseRepository;
        this.seatReservationRepositoryJpa = seatReservationRepositoryJpa;
        this.purchaseMapper = purchaseMapper;
        this.userService = userService;
        this.ticketPricingContext = ticketPricingContext;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PurchaseResponseDTO createPurchase(TicketPurchaseRequestDTO dto, String idempotencyKey) {
        User user = userService.getAuthenticatedUser();

        return purchaseRepository
                .findByIdempotencyKeyAndUser(idempotencyKey, user)
                .map(purchaseMapper::toResponseDTO)
                .orElseGet(() -> processNewPurchase(dto, idempotencyKey, user));
    }

    private PurchaseResponseDTO processNewPurchase(TicketPurchaseRequestDTO dto, String idempotencyKey, User user) {

        Purchase purchase = new Purchase(user, idempotencyKey);

        for (TicketItemDTO item : dto.getItems()) {

            SeatReservation reservation = seatReservationRepositoryJpa.findByIdAndUser(item.getReservationId(), user)
                            .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada ou não pertence ao usuário"));

            if (reservation.getStatus() != ReservationStatus.RESERVED) {
                throw new IllegalStateException("Reserva inválida para consumo");
            }

            BigDecimal price = ticketPricingContext.calculate(item.getTicketCategory(), reservation.getMovieSession());

            purchase.addTicket(reservation, item.getTicketCategory(), price);

            reservation.setStatus(ReservationStatus.CONSUMED);
        }

        Purchase saved = purchaseRepository.save(purchase);


        PurchaseCreatedNotificationData purchaseCreatedNotificationData = new PurchaseCreatedNotificationData(purchase.getId(), user.getName(), purchase.getPurchaseDate(), purchase.getTotalPrice(), user.getEmail());

        eventPublisher.publishEvent(new PurchaseCreatedEvent(this, purchaseCreatedNotificationData));

        return purchaseMapper.toResponseDTO(saved);
    }

    @Transactional
    public void modificarIdempotencyKeyPurchase(Long idPurchase, String idempotencyKey) {

        int updated = purchaseRepository
                .updateIdempotencyKey(idPurchase, idempotencyKey);

        if (updated == 0) {
            throw new ResourceNotFoundException(
                    "Purchase " + idPurchase + " não encontrada."
            );
        }
    }

}
