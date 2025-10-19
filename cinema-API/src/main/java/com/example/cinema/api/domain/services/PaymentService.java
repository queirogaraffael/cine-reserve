package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.Payment;
import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.PaymentStatus;
import com.example.cinema.api.domain.enums.PaymentType;
import com.example.cinema.api.domain.payment.strategy.PaymentStrategy;
import com.example.cinema.api.infrastructure.repositories.PaymentRepository;
import com.example.cinema.api.infrastructure.repositories.PurchaseRepository;
import com.example.cinema.api.shared.dtos.payment.requests.CardPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentMasterDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PixPaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PurchaseRepository purchaseRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final Map<PaymentType, PaymentStrategy> strategies;
    private final ObjectMapper objectMapper;

    public PaymentService(PurchaseRepository purchaseRepository,
                          PaymentRepository paymentRepository, UserService userService,
                          List<PaymentStrategy> strategies, ObjectMapper objectMapper) {
        this.purchaseRepository = purchaseRepository;
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(PaymentStrategy::getType, Function.identity()));
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PaymentResponseDTO processPayment(Long purchaseId, PaymentMasterDTO paymentMasterDTO) {

        String idempotencyKey = UUID.randomUUID().toString();

        User user = userService.getAuthenticatedUser();

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found"));

        PaymentStrategy strategy = strategies.get(paymentMasterDTO.getPaymentMethod());
        if (strategy == null) {
            throw new UnsupportedOperationException("Método não suportado.");
        }

        PaymentRequestDTO details = createSpecificRequestDTO(paymentMasterDTO);

        // converter para o tipo correto de PaymentRequestDTO
        PaymentResponseDTO response = strategy.process(purchase, user, details, idempotencyKey);

        // TODO: Converter pro tipo correto de PaymentResponseDTO

        Payment payment = new Payment();

        payment.setPaymentMethod(paymentMasterDTO.getPaymentMethod());
        payment.setPurchase(purchase);

        payment.setTransactionId(response.getTransactionId());
        payment.setPaymentStatus(PaymentStatus.PENDING);

        payment.setPaymentDate(LocalDateTime.now());

        paymentRepository.save(payment);

        return response;
    }

    private PaymentRequestDTO createSpecificRequestDTO(PaymentMasterDTO masterDTO) {

        Object rawDetails = masterDTO.getPaymentDetails();

        if (masterDTO.getPaymentMethod() == PaymentType.PIX) {
            return objectMapper.convertValue(rawDetails, PixPaymentRequestDTO.class);

        } else if (masterDTO.getPaymentMethod() == PaymentType.CARD) {
            return objectMapper.convertValue(rawDetails, CardPaymentRequestDTO.class);

        }
        throw new IllegalArgumentException("Detalhes de pagamento inválidos para o tipo: " + masterDTO.getPaymentMethod());
    }


}