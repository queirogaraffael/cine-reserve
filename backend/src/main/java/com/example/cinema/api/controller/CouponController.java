package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.coupon.CouponResponseDTO;
import com.example.cinema.api.application.service.CouponService;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Coupons")
@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @Operation(summary = "Obter cupons disponíveis do usuário")
    @ApiResponse(responseCode = "200", description = "Cupons obtidos com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/me")
    public ResponseEntity<List<CouponResponseDTO>> getMyCoupons(@AuthenticationPrincipal AuthenticatedUser principal) {
        List<CouponResponseDTO> coupons = couponService.getAvailableCouponsByUserId(principal.getId());
        return ResponseEntity.ok(coupons);
    }
}
