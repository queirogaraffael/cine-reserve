package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.coupon.CouponCreateDTO;
import com.example.cinema.api.application.dto.coupon.CouponResponseDTO;
import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.coupon.Coupon;
import com.example.cinema.api.domain.coupon.DiscountType;
import com.example.cinema.api.infrastructure.persistence.CinemaRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.CouponRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepositoryJpa couponRepository;

    @Mock
    private CinemaRepositoryJpa cinemaRepository;

    @Mock
    private UserRepositoryJpa userRepository;

    @InjectMocks
    private CouponService couponService;

    private UUID userId;
    private Long cinemaId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cinemaId = 1L;
    }

    @Test
    void shouldCreateCouponAsSuperAdmin_Global() {
        AuthenticatedUser superAdmin = new AuthenticatedUser(userId,
                List.of(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN")), null);

        CouponCreateDTO dto = new CouponCreateDTO(
                "GLOBAL10", "10% de desconto global", DiscountType.PERCENTAGE, new BigDecimal("10.0"), null, null, null);

        when(couponRepository.existsByCode("GLOBAL10")).thenReturn(false);
        when(couponRepository.save(any(Coupon.class))).thenAnswer(i -> {
            Coupon c = i.getArgument(0);
            c.setId(10L);
            return c;
        });

        CouponResponseDTO response = couponService.createCoupon(dto, superAdmin);

        assertNotNull(response);
        assertEquals("GLOBAL10", response.getCode());
        assertNull(response.getCinemaId());

        ArgumentCaptor<Coupon> couponCaptor = ArgumentCaptor.forClass(Coupon.class);
        verify(couponRepository).save(couponCaptor.capture());
        assertNull(couponCaptor.getValue().getCinema());
    }

    @Test
    void shouldForceCinemaIdForCinemaAdmin() {
        AuthenticatedUser cinemaAdmin = new AuthenticatedUser(userId,
                List.of(new SimpleGrantedAuthority("ROLE_CINEMA_ADMIN")), cinemaId);

        CouponCreateDTO dto = new CouponCreateDTO(
                null, "Desconto fixo", DiscountType.FIXED_VALUE, new BigDecimal("5.0"), null, null, null);

        when(couponRepository.existsByCode(anyString())).thenReturn(false);
        when(cinemaRepository.findById(cinemaId)).thenReturn(Optional.of(new Cinema()));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(i -> {
            Coupon c = i.getArgument(0);
            c.setId(20L);
            return c;
        });

        CouponResponseDTO response = couponService.createCoupon(dto, cinemaAdmin);

        assertNotNull(response.getCode());
        assertTrue(response.getCode().startsWith("CINE-"));

        ArgumentCaptor<Coupon> couponCaptor = ArgumentCaptor.forClass(Coupon.class);
        verify(couponRepository).save(couponCaptor.capture());
        assertNotNull(couponCaptor.getValue().getCinema());
    }

    @Test
    void shouldThrowExceptionIfCinemaIdNotFound() {
        AuthenticatedUser cinemaAdmin = new AuthenticatedUser(userId,
                List.of(new SimpleGrantedAuthority("ROLE_CINEMA_ADMIN")), cinemaId);

        CouponCreateDTO dto = new CouponCreateDTO(
                null, "Desconto fixo", DiscountType.FIXED_VALUE, new BigDecimal("5.0"), null, null, null);

        when(couponRepository.existsByCode(anyString())).thenReturn(false);
        when(cinemaRepository.findById(cinemaId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> couponService.createCoupon(dto, cinemaAdmin));
    }

    @Test
    void shouldToggleCouponStatusAsSuperAdmin() {
        AuthenticatedUser superAdmin = new AuthenticatedUser(userId,
                List.of(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN")), null);

        Coupon coupon = new Coupon();
        coupon.setId(10L);
        coupon.activate();

        when(couponRepository.findById(10L)).thenReturn(Optional.of(coupon));

        couponService.toggleCouponStatus(10L, false, superAdmin);

        assertFalse(coupon.isActive());
        verify(couponRepository).save(coupon);
    }

    @Test
    void shouldFailToggleCouponStatusForCinemaAdminOtherCinema() {
        AuthenticatedUser cinemaAdmin = new AuthenticatedUser(userId,
                List.of(new SimpleGrantedAuthority("ROLE_CINEMA_ADMIN")), 99L);

        Cinema cinema = new Cinema();
        org.springframework.test.util.ReflectionTestUtils.setField(cinema, "id", 1L);

        Coupon coupon = new Coupon();
        coupon.setId(10L);
        coupon.setCinema(cinema);

        when(couponRepository.findById(10L)).thenReturn(Optional.of(coupon));

        assertThrows(SecurityException.class, () -> couponService.toggleCouponStatus(10L, false, cinemaAdmin));
    }
}
