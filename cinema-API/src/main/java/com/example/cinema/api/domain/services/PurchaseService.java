package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.*;
import com.example.cinema.api.domain.enums.ReservationStatus;
import com.example.cinema.api.domain.pricing.context.TicketPricingContext;
import com.example.cinema.api.domain.purchase.event.PurchaseCreatedEvent;
import com.example.cinema.api.infrastructure.persistence.PurchaseRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.SeatReservationRepositoryJpa;
import com.example.cinema.api.shared.dtos.purchase.PurchaseResponseDTO;
import com.example.cinema.api.shared.dtos.purchase.TicketItemDTO;
import com.example.cinema.api.shared.dtos.purchase.TicketPurchaseRequestDTO;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import com.example.cinema.api.shared.exceptions.SeatReservationExpiredException;
import com.example.cinema.api.shared.mappers.PurchaseMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;

import java.util.Set;

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
    public PurchaseResponseDTO createPurchase(TicketPurchaseRequestDTO ticketPurchaseRequestDTO, String idempotencyKey) {

        User user = userService.getAuthenticatedUser();

        Set<Ticket> tickets = new HashSet<>();
        Purchase purchase = new Purchase();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for(TicketItemDTO item: ticketPurchaseRequestDTO.getItems()) {

            SeatReservation reserva = seatReservationRepositoryJpa.findById(item.getReservationId()).orElseThrow(()-> new ResourceNotFoundException("SeatReservation com id " + item.getReservationId() + " não encontrado."));

            if(reserva.isExpired()){
                throw new SeatReservationExpiredException("SeatReservation expirada!");
            }

            reserva.setStatus(ReservationStatus.CONSUMED);

            seatReservationRepositoryJpa.save(reserva);

            Ticket ticket = new Ticket();

            ticket.setSeatNumber(reserva.getSeatNumber());
            ticket.setUser(user);
            ticket.setCategory(item.getTicketCategory());
            ticket.setMovieSession(reserva.getMovieSession());

            tickets.add(ticket);

            BigDecimal ticketPrice = ticketPricingContext.calculate(item.getTicketCategory(), reserva.getMovieSession());

            totalPrice = totalPrice.add(ticketPrice);

        }

        purchase.setTickets(tickets);
        purchase.setIdempotencyKey(idempotencyKey);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setUser(user);
        purchase.setTotalPrice(totalPrice);

        Purchase savedPurchase = purchaseRepository.save(purchase);

        eventPublisher.publishEvent(new PurchaseCreatedEvent(this, user, savedPurchase));

        return purchaseMapper.toResponseDTO(savedPurchase);

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
