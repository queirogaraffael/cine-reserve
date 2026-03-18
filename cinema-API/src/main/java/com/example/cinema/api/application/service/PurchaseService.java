package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.purchase.exception.PurchaseNotFoundException;
import com.example.cinema.api.domain.seatreservation.exception.ReservationNotFoundException;
import com.example.cinema.api.domain.purchase.exception.PurchaseAlreadyHasPaymentException;
import com.example.cinema.api.application.dto.purchase.PurchaseIdempotencyResponseDTO;
import com.example.cinema.api.domain.seatreservation.exception.ReservationCannotBeConsumedException;
import com.example.cinema.api.domain.movie.exception.MovieSessionNotAvailableForPurchaseException;
import com.example.cinema.api.domain.purchase.PurchaseStatus;
import com.example.cinema.api.domain.seatreservation.ReservationStatus;
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
                            .orElseThrow(() -> new ReservationNotFoundException("Reserva não encontrada ou não pertence ao usuário"));

            if (reservation.getStatus() != ReservationStatus.RESERVED) {
                throw new ReservationCannotBeConsumedException("Reserva inválida para consumo");
            }

            if(!reservation.getMovieSession().isAvailableForPurchase()){
                throw new MovieSessionNotAvailableForPurchaseException("Movie Session: " + reservation.getMovieSession().getId() +" não está disponivel para compra.");
            }

            BigDecimal price = ticketPricingContext.calculate(item.getTicketCategory(), reservation.getMovieSession());

            purchase.addTicket(reservation, item.getTicketCategory(), price);

            reservation.consume();
        }

        purchase.moveToStatus(PurchaseStatus.WAITING_PAYMENT);

        Purchase saved = purchaseRepository.save(purchase);

        eventPublisher.publishEvent(new PurchaseCreatedEvent(saved.getId(), user.getId(), saved.getTotalPrice(), saved.getPurchaseDate()));

        return purchaseMapper.toResponseDTO(saved);
    }

    @Transactional
    public PurchaseIdempotencyResponseDTO modificarIdempotencyKeyPurchase(Long idPurchase, String idempotencyKey) {

        User user = userService.getAuthenticatedUser();

        Purchase purchase = purchaseRepository.findByIdAndUser(idPurchase, user).orElseThrow(() -> new PurchaseNotFoundException("Compra não encontrada."));

        if(purchase.getPayment() != null){
            throw new PurchaseAlreadyHasPaymentException("IdempotencyKey não pode ser modificada porque um Pagamento já está associado.");
        }

        purchase.changeIdempotencyKey(idempotencyKey);

        Purchase purchaseSaved = purchaseRepository.save(purchase);

        return purchaseMapper.toPurchaseIdempotencyResponseDTO(purchaseSaved);
    }

}
