package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.coupon.CouponCreateDTO;
import com.example.cinema.api.application.dto.coupon.CouponResponseDTO;
import com.example.cinema.api.application.service.CouponService;
import com.example.cinema.api.domain.coupon.DiscountType;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import com.example.cinema.api.infrastructure.security.SecurityConfigurations;
import com.example.cinema.api.infrastructure.security.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponAdminController.class)
@Import(SecurityConfigurations.class)

class CouponAdminControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private CouponService couponService;

        @MockBean
        private TokenService tokenService;

        @Test
        void shouldCreateCouponAsSuperAdmin() throws Exception {
                UUID adminId = UUID.randomUUID();
                AuthenticatedUser superAdmin = new AuthenticatedUser(adminId,
                                List.of(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN")), null);

                CouponCreateDTO reqDto = new CouponCreateDTO(
                                "CODE123", "Desc", DiscountType.FIXED_VALUE, new BigDecimal("10.0"), null, null, null);

                CouponResponseDTO resDto = CouponResponseDTO.builder()
                                .id(1L)
                                .code("CODE123")
                                .description("Desc")
                                .discountType(DiscountType.FIXED_VALUE)
                                .build();

                when(couponService.createCoupon(any(), eq(superAdmin))).thenReturn(resDto);

                mockMvc.perform(post("/api/admin/coupons")
                                .with(user(superAdmin))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reqDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.code").value("CODE123"));
        }

        @Test
        void shouldReturn403ForNormalUser() throws Exception {
                UUID userId = UUID.randomUUID();
                AuthenticatedUser normalUser = new AuthenticatedUser(userId,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")), null);

                CouponCreateDTO reqDto = new CouponCreateDTO(
                                "CODE123", "Desc", DiscountType.FIXED_VALUE, new BigDecimal("10.0"), null, null, null);

                mockMvc.perform(post("/api/admin/coupons")
                                .with(user(normalUser))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reqDto)))
                                .andExpect(status().isForbidden());
        }

        @Test
        void shouldToggleCouponStatusAsSuperAdmin() throws Exception {
                UUID adminId = UUID.randomUUID();
                AuthenticatedUser superAdmin = new AuthenticatedUser(adminId,
                                List.of(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN")), null);

                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/admin/coupons/1/status")
                                .param("active", "false")
                                .with(user(superAdmin)))
                                .andExpect(status().isNoContent());
        }
}
