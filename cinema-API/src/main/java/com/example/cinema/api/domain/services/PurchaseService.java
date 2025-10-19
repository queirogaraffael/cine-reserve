package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.Ticket;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.PaymentType;
import com.example.cinema.api.domain.enums.UserCategory;
import com.example.cinema.api.domain.payment.strategy.PaymentStrategy;
import com.example.cinema.api.domain.pricing.context.TicketPricingContext;
import com.example.cinema.api.domain.pricing.strategy.PricingStrategy;
import com.example.cinema.api.domain.purchase.event.PurchaseCreatedEvent;
import com.example.cinema.api.infrastructure.repositories.MovieSessionRepository;
import com.example.cinema.api.infrastructure.repositories.PurchaseRepository;
import com.example.cinema.api.infrastructure.repositories.TicketRepository;
import com.example.cinema.api.shared.dtos.purchase.PurchaseRequestDTO;
import com.example.cinema.api.shared.dtos.purchase.PurchaseResponseDTO;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import com.example.cinema.api.shared.mappers.PurchaseMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final MovieSessionRepository movieSessionRepository;
    private final PurchaseMapper purchaseMapper;
    private final UserService userService;
    private final TicketPricingContext ticketPricingContext;
    private final ApplicationEventPublisher eventPublisher;


    public PurchaseService(PurchaseRepository purchaseRepository, TicketService ticketService, TicketRepository ticketRepository, MovieSessionRepository movieSessionRepository, PurchaseMapper purchaseMapper, UserService userService, TicketPricingContext ticketPricingContext, ApplicationEventPublisher eventPublisher) {
        this.purchaseRepository = purchaseRepository;
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
        this.movieSessionRepository = movieSessionRepository;
        this.purchaseMapper = purchaseMapper;
        this.userService = userService;
        this.ticketPricingContext = ticketPricingContext;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    // TODO: REFATORAR PARA MELHORAR A LOGICA DE CALCULO DE PRECO
    public PurchaseResponseDTO createPurchase(PurchaseRequestDTO purchaseRequestDTO) {

        User user = userService.getAuthenticatedUser();

        Set<Ticket> tickets = new HashSet<>();
        Purchase purchase = new Purchase();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for(Long ticketId : purchaseRequestDTO.getTicketIds()) {
            Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new ResourceNotFoundException("Ticket nao encontrado"));
            tickets.add(ticket);

            MovieSession movieSession = movieSessionRepository.findById(ticket.getMovieSession().getId()).orElseThrow(() -> new ResourceNotFoundException("MovieSession nao encontrado"));

            BigDecimal ticketPrice = ticketPricingContext.executeStrategy(user, movieSession);

            totalPrice = totalPrice.add(ticketPrice);

        }

        purchase.setTickets(tickets);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setUser(user);
        purchase.setTotalPrice(totalPrice);

        Purchase savedPurchase = purchaseRepository.save(purchase);

        eventPublisher.publishEvent(new PurchaseCreatedEvent(this, user, savedPurchase));

        return purchaseMapper.toResponseDTO(savedPurchase);

    }
}
