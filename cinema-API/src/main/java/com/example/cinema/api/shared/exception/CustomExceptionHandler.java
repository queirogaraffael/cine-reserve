package com.example.cinema.api.shared.exception;

import com.example.cinema.api.domain.payment.exception.PaymentMethodRequiredException;
import com.example.cinema.api.domain.payment.exception.PurchaseRequiredException;
import com.example.cinema.api.application.exception.RoomScheduleConflictException;
import com.example.cinema.api.application.exception.*;
import com.example.cinema.api.domain.movie.exception.InvalidSessionTimeRangeException;
import com.example.cinema.api.domain.purchase.exception.PurchaseAlreadyHasPaymentException;
import com.example.cinema.api.domain.seatreservation.exception.ReservationCannotBeCancelledException;
import com.example.cinema.api.domain.ticket.exception.SeatAlreadyReservedException;
import com.example.cinema.api.domain.genre.exception.GenreAlreadyExistsException;
import com.example.cinema.api.domain.room.exception.NumeroDeQuartoJaCadastradoException;
import com.example.cinema.api.domain.seatreservation.exception.SeatReservationExpiredException;
import com.example.cinema.api.domain.user.exception.UserAlreadyExistsException;
import com.example.cinema.api.infrastructure.exception.ApiPagamentoException;
import com.example.cinema.api.infrastructure.exception.WebhookException;
import com.example.cinema.api.infrastructure.exception.EmailSendException;
import com.example.cinema.api.infrastructure.security.exception.RefreshTokenInvalidException;
import com.example.cinema.api.infrastructure.security.exception.TokenCreationException;
import com.example.cinema.api.infrastructure.security.exception.TokenValidationException;
import com.mercadopago.exceptions.MPApiException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {

    public record ErrorResponse(String message, int status, String timestamp) {}

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        String safeMessage = "Credenciais de acesso inválidas (usuário ou senha incorretos).";

        ErrorResponse response = new ErrorResponse(
                safeMessage,
                HttpStatus.UNAUTHORIZED.value(),
                new Date().toString()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GenreAlreadyExistsException.class)
    public ResponseEntity<Object> handleGeneroJaExisteException(GenreAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NumeroDeQuartoJaCadastradoException.class)
    public ResponseEntity<Object> handleNumeroDeQuartoJaCadastradoException(NumeroDeQuartoJaCadastradoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TokenCreationException.class)
    public ResponseEntity<Object> handleTokenCreationException(TokenCreationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<Object> handleEmailSendException(EmailSendException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<Object> handleTokenValidationException(TokenValidationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        String genericMessage = "Ocorreu um erro interno inesperado no servidor.";
        return new ResponseEntity<>(genericMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Object> handleUnsupportedOperationException(UnsupportedOperationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiPagamentoException.class)
    public ResponseEntity<Object> handleApiPagamentoException(ApiPagamentoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(WebhookException.class)
    public ResponseEntity<Object> handleWebhookException(WebhookException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MPApiException.class)
    public ResponseEntity<Object> handleMercadoPagoException(MPApiException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(SeatAlreadyReservedException.class)
    public ResponseEntity<?> handleDataIntegrity(SeatAlreadyReservedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Assento já reservado.");
    }

    @ExceptionHandler(SeatReservationExpiredException.class)
    public ResponseEntity<?> handleSeatReservationExpired(SeatReservationExpiredException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(PurchaseAlreadyHasPaymentException.class)
    public ResponseEntity<?> handlePurchaseAlreadyHasPaymentException(PurchaseAlreadyHasPaymentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenInvalidException.class)
    public ResponseEntity<?> handleRefreshTokenInvalidException(RefreshTokenInvalidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidReservationStatusException.class)
    public ResponseEntity<?> handleInvalidReservationStatusException(InvalidReservationStatusException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidSeatNumberException.class)
    public ResponseEntity<?> handleInvalidSeatNumberException(InvalidSeatNumberException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(SessionNotAvailableForPurchaseException.class)
    public ResponseEntity<?> handleSessionNotAvailableForPurchaseException(SessionNotAvailableForPurchaseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ReservationCannotBeCancelledException.class)
    public ResponseEntity<?> handleReservationCannotBeCancelledException(ReservationCannotBeCancelledException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidSessionTimeRangeException.class)
    public ResponseEntity<?> handleInvalidSessionTimeRangeException(InvalidSessionTimeRangeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RoomScheduleConflictException.class)
    public ResponseEntity<?> handleRoomScheduleConflictException(RoomScheduleConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(PurchaseRequiredException.class)
    public ResponseEntity<String> handlePurchaseRequired(PurchaseRequiredException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(PaymentMethodRequiredException.class)
    public ResponseEntity<String> handlePaymentMethodRequired(PaymentMethodRequiredException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}