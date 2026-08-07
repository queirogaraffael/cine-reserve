package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.order.*;
import com.example.cinema.api.application.mapper.OrderMapper;
import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.movie.exception.MovieSessionNotFoundException;
import com.example.cinema.api.domain.movie.exception.MovieSessionNotAvailableForPurchaseException;
import com.example.cinema.api.domain.order.Order;
import com.example.cinema.api.domain.order.OrderItem;
import com.example.cinema.api.domain.order.OrderStatus;
import com.example.cinema.api.domain.order.event.OrderCreatedEvent;
import com.example.cinema.api.domain.order.exception.InvalidSeatSelectionException;
import com.example.cinema.api.domain.common.Money;
import com.example.cinema.api.domain.order.exception.OrderNotFoundException;
import com.example.cinema.api.domain.order.exception.SeatAlreadyReservedException;
import com.example.cinema.api.domain.payment.OrderPayment;
import com.example.cinema.api.domain.room.Seat;
import com.example.cinema.api.domain.seatreservation.SeatReservation;
import com.example.cinema.api.domain.ticket.TicketType;
import com.example.cinema.api.domain.ticket.exception.TicketTypeNotFoundException;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.infrastructure.persistence.MovieSessionRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.OrderPaymentRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.OrderRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.SeatRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.SeatReservationRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.TicketTypeRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.PromotionRepositoryJpa;
import com.example.cinema.api.domain.promotion.Promotion;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final int RESERVATION_TTL_MINUTES = 10;
    private static final BigDecimal SERVICE_FEE_PER_TICKET = new BigDecimal("2.50");

    private final OrderRepositoryJpa orderRepository;
    private final MovieSessionRepositoryJpa movieSessionRepository;
    private final TicketTypeRepositoryJpa ticketTypeRepository;
    private final SeatRepositoryJpa seatRepository;
    private final SeatReservationRepositoryJpa seatReservationRepository;
    private final PromotionRepositoryJpa promotionRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final ApplicationEventPublisher eventPublisher;

    private final OrderPaymentRepositoryJpa orderPaymentRepository;

    public OrderService(OrderRepositoryJpa orderRepository,
                        MovieSessionRepositoryJpa movieSessionRepository,
                        TicketTypeRepositoryJpa ticketTypeRepository,
                        SeatRepositoryJpa seatRepository,
                        SeatReservationRepositoryJpa seatReservationRepository,
                        PromotionRepositoryJpa promotionRepository,
                        UserService userService,
                        OrderMapper orderMapper,
                        ApplicationEventPublisher eventPublisher,
                        OrderPaymentRepositoryJpa orderPaymentRepository) {
        this.orderRepository = orderRepository;
        this.movieSessionRepository = movieSessionRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.seatRepository = seatRepository;
        this.seatReservationRepository = seatReservationRepository;
        this.promotionRepository = promotionRepository;
        this.userService = userService;
        this.orderMapper = orderMapper;
        this.eventPublisher = eventPublisher;
        this.orderPaymentRepository = orderPaymentRepository;
    }

    @Transactional(readOnly = true)
    public Page<OrderHistoryResponseDTO> getUserOrderHistory(UUID userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findAllByUserIdAndStatusNotOrderByCreatedAtDesc(userId, OrderStatus.CREATED, pageable);

        if (orders.isEmpty()) {
            return orders.map(order -> new OrderHistoryResponseDTO());
        }

        List<Long> orderIds = orders.getContent().stream().map(Order::getId).collect(Collectors.toList());

        List<SeatReservation> allSeats = seatReservationRepository.findAllByOrderIdIn(orderIds);
        Map<Long, List<SeatReservation>> seatsByOrderId = allSeats.stream()
                .collect(Collectors.groupingBy(res -> res.getOrder().getId()));

        List<OrderPayment> allPayments = orderPaymentRepository.findAllByOrderIdIn(orderIds);
        Map<Long, OrderPayment> paymentByOrderId = allPayments.stream()
                .collect(Collectors.toMap(p -> p.getOrder().getId(), p -> p));

        return orders.map(order -> {
            List<SeatReservation> orderSeats = seatsByOrderId.getOrDefault(order.getId(), java.util.Collections.emptyList());
            String seatsStr = orderSeats.stream().map(res -> res.getSeat().getCode()).collect(Collectors.joining(", "));

            OrderPayment payment = paymentByOrderId.get(order.getId());
            String paymentStatus = (payment != null && payment.getPaymentStatus() != null) ? payment.getPaymentStatus().name() : null;

            boolean isCancelled = order.getStatus() == OrderStatus.CANCELLED;
            
            boolean paymentFailedOrPending = paymentStatus == null || !paymentStatus.equals("APPROVED");
            boolean canRetryPayment = order.getStatus() == OrderStatus.WAITING_PAYMENT && paymentFailedOrPending;

            return com.example.cinema.api.application.dto.order.OrderHistoryResponseDTO.builder()
                    .orderId(order.getId())
                    .exhibitionTitle(order.getMovieSession().getMovieExhibition().getTitle())
                    .movieImageUrl(order.getMovieSession().getMovieExhibition().getMovie().getImageUrl())
                    .sessionDate(java.time.LocalDateTime.of(order.getMovieSession().getShowDate(), order.getMovieSession().getStartTime()))
                    .reservedAt(order.getCreatedAt())
                    .seats(seatsStr)
                    .total(order.getTotalPrice() != null ? order.getTotalPrice().getAmount() : BigDecimal.ZERO)
                    .paymentStatus(paymentStatus)
                    .isCancelled(isCancelled)
                    .canRetryPayment(canRetryPayment)
                    .build();
        });
    }

    @Transactional
    public OrderResponseDTO createOrder(CreateOrderRequestDTO dto, UUID userId) {
        User user = userService.findById(userId);

        MovieSession session = movieSessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new MovieSessionNotFoundException("Sessão não encontrada."));

        if (!session.isAvailableForPurchase()) {
            throw new MovieSessionNotAvailableForPurchaseException("Sessão não está disponível para compra.");
        }

        Order order = new Order(user, session);

        Map<Long, Integer> groupedItems = dto.getItems().stream()
                .collect(Collectors.toMap(
                        OrderItemRequestDTO::getTicketTypeId,
                        OrderItemRequestDTO::getQuantity,
                        Integer::sum
                ));

        List<Promotion> activePromotions = promotionRepository.findByCinemaIdAndActiveTrue(session.getCinemaRoom().getCinema().getId());
        Promotion appliedPromotion = activePromotions.stream().filter(p -> p.appliesTo(session)).findFirst().orElse(null);

        for (Map.Entry<Long, Integer> entry : groupedItems.entrySet()) {
            TicketType ticketType = ticketTypeRepository.findById(entry.getKey())
                    .orElseThrow(() -> new TicketTypeNotFoundException("Tipo de ingresso não encontrado: " + entry.getKey()));

            BigDecimal unitPrice = ticketType.getPrice();
            
            if (appliedPromotion != null) {
                if (appliedPromotion.getFixedPrice() != null) {
                    unitPrice = appliedPromotion.getFixedPrice();
                } else if (appliedPromotion.getDiscountPercentage() != null) {
                    unitPrice = unitPrice.multiply(BigDecimal.ONE.subtract(appliedPromotion.getDiscountPercentage().divide(BigDecimal.valueOf(100))));
                }
            }

            OrderItem item = new OrderItem(order, ticketType, entry.getValue(), unitPrice);
            order.addItem(item);
        }

        BigDecimal serviceFee = SERVICE_FEE_PER_TICKET.multiply(BigDecimal.valueOf(order.getTotalTicketsCount()));
        order.applyServiceFee(Money.of(serviceFee));

        Order saved = orderRepository.save(order);

        eventPublisher.publishEvent(new OrderCreatedEvent(saved.getId(), user.getId(), saved.getTotalPrice().getAmount(), saved.getCreatedAt()));

        return orderMapper.toResponseDTO(saved);
    }

    @Transactional
    public OrderResponseDTO selectSeats(Long orderId, OrderSeatSelectionRequestDTO dto, UUID userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado."));

        Set<Long> seatIds = dto.getSeatIds();

        if (seatIds.size() != order.getTotalTicketsCount()) {
            throw new InvalidSeatSelectionException(
                    "Quantidade de assentos inválida. Esperado: " + order.getTotalTicketsCount() + ", recebido: " + seatIds.size()
            );
        }

        List<SeatReservation> existingReservations = seatReservationRepository
                .findActiveReservationsBySeatIdsAndSession(new ArrayList<>(seatIds), order.getMovieSession().getId());

        if (!existingReservations.isEmpty()) {
            String takenCodes = existingReservations.stream()
                    .map(r -> r.getSeat().getCode())
                    .collect(Collectors.joining(", "));
            throw new SeatAlreadyReservedException("Assento(s) já reservado(s): " + takenCodes);
        }

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(RESERVATION_TTL_MINUTES);

        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new InvalidSeatSelectionException("Assento não encontrado: " + seatId));

            SeatReservation reservation = new SeatReservation(order.getMovieSession(), seat, order);
            seatReservationRepository.save(reservation);
        }

        order.markReservationExpiry(expiresAt);
        order.moveToStatus(OrderStatus.WAITING_PAYMENT);

        Order saved = orderRepository.save(order);

        return orderMapper.toResponseDTO(saved);
    }
}
