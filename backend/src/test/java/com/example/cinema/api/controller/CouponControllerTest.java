package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.coupon.CouponResponseDTO;
import com.example.cinema.api.application.service.CouponService;
import com.example.cinema.api.domain.coupon.CouponStatus;
import com.example.cinema.api.domain.coupon.DiscountType;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import com.example.cinema.api.infrastructure.security.SecurityConfigurations;
import com.example.cinema.api.infrastructure.security.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
@Import(SecurityConfigurations.class)

class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @MockBean
    private TokenService tokenService;

    @Test
    void shouldReturnMyCoupons() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticatedUser authUser = new AuthenticatedUser(userId, List.of(new SimpleGrantedAuthority("ROLE_USER")),
                null);

        CouponResponseDTO dto = CouponResponseDTO.builder()
                .id(1L)
                .code("CINE-TEST")
                .description("Test Coupon")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(new BigDecimal("10.0"))
                .userId(userId)
                .status(CouponStatus.AVAILABLE)
                .build();

        when(couponService.getAvailableCouponsByUserId(userId)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/coupons/me")
                .with(user(authUser))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("CINE-TEST"))
                .andExpect(jsonPath("$[0].description").value("Test Coupon"));
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/coupons/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
