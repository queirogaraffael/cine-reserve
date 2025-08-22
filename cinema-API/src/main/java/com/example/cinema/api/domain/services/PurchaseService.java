package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.Ticket;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.purchase.event.PurchaseCreatedEvent;
import com.example.cinema.api.domain.repositories.MovieSessionRepository;
import com.example.cinema.api.domain.repositories.PurchaseRepository;
import com.example.cinema.api.domain.repositories.TicketRepository;
import com.example.cinema.api.shared.dtos.purchase.PurchaseRequestDTO;
import com.example.cinema.api.shared.dtos.purchase.PurchaseResponseDTO;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import com.example.cinema.api.shared.mappers.PurchaseMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final MovieSessionRepository movieSessionRepository;
    private final PurchaseMapper purchaseMapper;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    public PurchaseService(PurchaseRepository purchaseRepository, TicketService ticketService, TicketRepository ticketRepository, MovieSessionRepository movieSessionRepository, PurchaseMapper purchaseMapper, UserService userService, ApplicationEventPublisher eventPublisher) {
        this.purchaseRepository = purchaseRepository;
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
        this.movieSessionRepository = movieSessionRepository;
        this.purchaseMapper = purchaseMapper;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PurchaseResponseDTO createPurchase(PurchaseRequestDTO purchaseRequestDTO) {

        Ticket ticket = ticketRepository.findById(purchaseRequestDTO.getTicketId()).orElseThrow(() -> new ResourceNotFoundException("Ticket nao encontrado"));
        MovieSession movieSession = movieSessionRepository.findById(purchaseRequestDTO.getMovieSessionId()).orElseThrow(() -> new ResourceNotFoundException("MovieSession nao encontrado"));

        Purchase purchase = purchaseMapper.toEntity(purchaseRequestDTO);
        purchase.setTicket(ticket);
        purchase.setMovieSession(movieSession);
        purchase.setPurchaseDate(LocalDateTime.now());

        User user = userService.getAuthenticatedUser();

        purchase.setUser(user);

        BigDecimal ticketPrice = ticketService.calculateTicketPrice(user.getCategory(), movieSession);

        purchase.setTotalPrice(ticketPrice);

        Purchase savedPurchase = purchaseRepository.save(purchase);

        eventPublisher.publishEvent(new PurchaseCreatedEvent(this, savedPurchase));

        return purchaseMapper.toResponseDTO(savedPurchase);

    }
}
