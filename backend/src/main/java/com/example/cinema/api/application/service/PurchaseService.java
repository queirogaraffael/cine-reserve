package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.purchase.exception.PurchaseNotFoundException;
import com.example.cinema.api.domain.seatreservation.exception.ReservationNotFoundException;
import com.example.cinema.api.application.dto.purchase.PurchaseIdempotencyResponseDTO;
import com.example.cinema.api.domain.movie.exception.MovieSessionNotAvailableForPurchaseException;
import com.example.cinema.api.domain.purchase.PurchaseStatus;
import com.example.cinema.api.domain.seatreservation.SeatReservation;
import com.example.cinema.api.domain.ticket.context.TicketPricingContext;
import com.example.cinema.api.domain.purchase.Purchase;
import com.example.cinema.api.domain.purchase.event.PurchaseCreatedEvent;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.infrastructure.persistence.PurchaseRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.SeatReservationRepositoryJpa;
import com.example.cinema.api.application.dto.purchase.PurchaseResponseDTO;
import com.example.cinema.api.application.dto.purchase.TicketItemDTO;
import com.example.cinema.api.application.dto.purchase.TicketPurchaseRequestDTO;
import com.example.cinema.api.application.mapper.PurchaseMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

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
    public PurchaseResponseDTO createPurchase(TicketPurchaseRequestDTO dto, String idempotencyKey, UUID userId) {

        return purchaseRepository
                .findByIdempotencyKeyAndUserId(idempotencyKey, userId)
                .map(purchaseMapper::toResponseDTO)
                .orElseGet(() -> processNewPurchase(dto, idempotencyKey, userId));
    }

    private PurchaseResponseDTO processNewPurchase(TicketPurchaseRequestDTO dto, String idempotencyKey, UUID userId) {

        User user = userService.findById(userId);

        Purchase purchase = new Purchase(user, idempotencyKey);

        for (TicketItemDTO item : dto.getItems()) {

            SeatReservation reservation = seatReservationRepositoryJpa.findByIdAndUserId(item.getReservationId(), userId)
                    .orElseThrow(() -> new ReservationNotFoundException("Reserva não encontrada ou não pertence ao usuário"));

            reservation.consume();

            if (!reservation.getMovieSession().isAvailableForPurchase()) {
                throw new MovieSessionNotAvailableForPurchaseException("Movie Session: " + reservation.getMovieSession().getId() + " não está disponivel para compra.");
            }

            BigDecimal price = ticketPricingContext.calculate(item.getTicketCategory(), reservation.getMovieSession());

            purchase.addTicket(reservation, item.getTicketCategory(), price);
        }

        purchase.moveToStatus(PurchaseStatus.WAITING_PAYMENT);

        Purchase saved = purchaseRepository.save(purchase);

        eventPublisher.publishEvent(new PurchaseCreatedEvent(saved.getId(), user.getId(), saved.getTotalPrice(), saved.getPurchaseDate()));

        return purchaseMapper.toResponseDTO(saved);
    }

    @Transactional
    public PurchaseIdempotencyResponseDTO modificarIdempotencyKeyPurchase(Long idPurchase, String idempotencyKey, UUID userId) {

        Purchase purchase = purchaseRepository.findByIdAndUserId(idPurchase, userId)
                .orElseThrow(() -> new PurchaseNotFoundException("Compra não encontrada."));

        purchase.changeIdempotencyKey(idempotencyKey);

        Purchase purchaseSaved = purchaseRepository.save(purchase);

        return purchaseMapper.toPurchaseIdempotencyResponseDTO(purchaseSaved);
    }

}
