package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.coupon.CouponCreateDTO;
import com.example.cinema.api.application.dto.coupon.CouponResponseDTO;
import com.example.cinema.api.application.service.CouponService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Coupons Admin")
@RestController
@RequestMapping("/api/admin/coupons")
public class CouponAdminController {

    private final CouponService couponService;

    public CouponAdminController(CouponService couponService) {
        this.couponService = couponService;
    }

    @Operation(summary = "Criar um novo cupom")
    @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "403", description = "Acesso negado")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<CouponResponseDTO> createCoupon(
            @RequestBody @Valid CouponCreateDTO data,
            @AuthenticationPrincipal AuthenticatedUser adminUser) {

        CouponResponseDTO created = couponService.createCoupon(data, adminUser);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{code}")
                .buildAndExpand(created.getCode())
                .toUri();

        return ResponseEntity.created(uri).body(created);
    }

    @Operation(summary = "Ativar ou desativar um cupom")
    @ApiResponse(responseCode = "204", description = "Status do cupom alterado com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso negado")
    @ApiResponse(responseCode = "404", description = "Cupom não encontrado")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> toggleCouponStatus(
            @PathVariable Long id,
            @RequestParam boolean active,
            @AuthenticationPrincipal AuthenticatedUser adminUser) {

        couponService.toggleCouponStatus(id, active, adminUser);
        return ResponseEntity.noContent().build();
    }
}
