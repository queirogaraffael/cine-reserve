package com.example.cinema.api.shared.exception;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        String message,
        int status,
        String path,
        LocalDateTime timestamp
) {}