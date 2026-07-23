package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.user.ConfirmEmailDTO;
import com.example.cinema.api.application.service.RegistrationConfirmationService;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Verification")
@RestController
@RequestMapping("/api/users/me/email-verification")
public class RegistrationController {

    private final RegistrationConfirmationService confirmationService;

    public RegistrationController(RegistrationConfirmationService confirmationService) {
        this.confirmationService = confirmationService;
    }

    @Operation(summary = "Confirm e-mail with 6-digit code")
    @ApiResponse(responseCode = "200", description = "E-mail confirmed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid or expired code")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "404", description = "Confirmation not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<Void> confirmEmail(@AuthenticationPrincipal AuthenticatedUser principal,
                                             @RequestBody @Valid ConfirmEmailDTO data) {
        confirmationService.confirmEmail(principal.getId(), data.getToken());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Resend verification code to e-mail")
    @ApiResponse(responseCode = "200", description = "Code resent successfully")
    @ApiResponse(responseCode = "400", description = "E-mail already confirmed")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/resend")
    public ResponseEntity<Void> resendCode(@AuthenticationPrincipal AuthenticatedUser principal) {
        confirmationService.resendCode(principal.getId());
        return ResponseEntity.ok().build();
    }
}