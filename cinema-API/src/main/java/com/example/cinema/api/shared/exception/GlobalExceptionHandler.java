package com.example.cinema.api.shared.exception;

import com.example.cinema.api.domain.exception.RefreshTokenInvalidException;
import com.example.cinema.api.domain.exception.ResourceNotFoundException;
import com.example.cinema.api.domain.exception.StateConflictException;
import com.example.cinema.api.domain.exception.ValidationException;
import com.example.cinema.api.infrastructure.exception.InfrastructureException;
import com.example.cinema.api.infrastructure.security.exception.TokenCreationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public record ApiErrorResponse(String message, int status, String path, LocalDateTime timestamp) {}

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(String message, HttpStatus status, HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(message, status.value(), request.getRequestURI(), LocalDateTime.now());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(ValidationException ex, HttpServletRequest request) {

        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {

        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(StateConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(StateConflictException ex, HttpServletRequest request) {

        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {

        String safeMessage = "Credenciais de acesso inválidas (usuário ou senha incorretos).";

        return buildErrorResponse(safeMessage, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {

        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientAuthentication(InsufficientAuthenticationException ex, HttpServletRequest request) {

        return buildErrorResponse("Usuário não autenticado", HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleInternalError(Exception ex, HttpServletRequest request) {

        return buildErrorResponse("Unexpected internal error", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {

        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedOperationException(UnsupportedOperationException ex, HttpServletRequest request) {

        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException ex, HttpServletRequest request) {

        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        StringBuilder message = new StringBuilder();

        ex.getBindingResult().getFieldErrors().forEach(error -> {message.append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append("; ");});

        return buildErrorResponse(message.toString(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ApiErrorResponse> handleInfrastructure(InfrastructureException ex, HttpServletRequest request) {
        log.error("Infrastructure failure at {}: {}", request.getRequestURI(), ex.getMessage(), ex.getCause());
        return buildErrorResponse("An internal server error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(TokenCreationException.class)
    public ResponseEntity<ApiErrorResponse> handleTokenCreationException(TokenCreationException ex, HttpServletRequest request) {
        log.error("Failed to create JWT token", ex);
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(RefreshTokenInvalidException.class)
    public ResponseEntity<ApiErrorResponse> handleRefreshTokenInvalidException(RefreshTokenInvalidException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }
}