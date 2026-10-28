package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.coupon.CouponCreateDTO;
import com.example.cinema.api.application.dto.coupon.CouponResponseDTO;
import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.coupon.Coupon;
import com.example.cinema.api.domain.coupon.CouponStatus;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.infrastructure.persistence.CinemaRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.CouponRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CouponService {

    private final CouponRepositoryJpa couponRepository;
    private final CinemaRepositoryJpa cinemaRepository;
    private final UserRepositoryJpa userRepository;

    public CouponService(CouponRepositoryJpa couponRepository, CinemaRepositoryJpa cinemaRepository, UserRepositoryJpa userRepository) {
        this.couponRepository = couponRepository;
        this.cinemaRepository = cinemaRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CouponResponseDTO> getAvailableCouponsByUserId(UUID userId) {
        return couponRepository.findByUserIdAndStatusAndActiveTrue(userId, CouponStatus.AVAILABLE)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CouponResponseDTO createCoupon(CouponCreateDTO data, AuthenticatedUser adminUser) {
        String code = (data.getCode() != null && !data.getCode().isBlank()) 
                ? data.getCode() 
                : generateUniqueCode();

        if (couponRepository.existsByCode(code)) {
            throw new IllegalArgumentException("O código do cupom já existe");
        }

        Long finalCinemaId = data.getCinemaId();
        
        boolean isCinemaAdmin = adminUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CINEMA_ADMIN"));

        if (isCinemaAdmin) {
            finalCinemaId = adminUser.getCinemaId();
            if (finalCinemaId == null) {
                throw new IllegalStateException("O CINEMA_ADMIN não possui um cinema vinculado.");
            }
        }

        Cinema cinema = null;
        if (finalCinemaId != null) {
            cinema = cinemaRepository.findById(finalCinemaId)
                    .orElseThrow(() -> new IllegalArgumentException("ID do cinema não encontrado"));
        }

        User targetUser = null;
        if (data.getUserId() != null) {
            targetUser = userRepository.findById(data.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("ID do usuário não encontrado"));
        }

        Coupon coupon = Coupon.builder()
                .code(code)
                .description(data.getDescription())
                .discountType(data.getDiscountType())
                .discountValue(data.getDiscountValue())
                .expirationDate(data.getExpirationDate())
                .user(targetUser)
                .cinema(cinema)
                .status(CouponStatus.AVAILABLE)
                .build();

        Coupon saved = couponRepository.save(coupon);
        return mapToDTO(saved);
    }

    @Transactional
    public void toggleCouponStatus(Long id, boolean active, AuthenticatedUser adminUser) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado"));

        boolean isCinemaAdmin = adminUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CINEMA_ADMIN"));

        if (isCinemaAdmin) {
            Long adminCinemaId = adminUser.getCinemaId();
            if (coupon.getCinema() == null || !coupon.getCinema().getId().equals(adminCinemaId)) {
                throw new SecurityException("Sem permissão para alterar este cupom");
            }
        }

        if (active) {
            coupon.activate();
        } else {
            coupon.deactivate();
        }
        
        couponRepository.save(coupon);
    }

    private String generateUniqueCode() {
        String generated;
        do {
            generated = "CINE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (couponRepository.existsByCode(generated));
        return generated;
    }

    private CouponResponseDTO mapToDTO(Coupon coupon) {
        return CouponResponseDTO.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .expirationDate(coupon.getExpirationDate())
                .userId(coupon.getUser() != null ? coupon.getUser().getId() : null)
                .cinemaId(coupon.getCinema() != null ? coupon.getCinema().getId() : null)
                .status(coupon.getStatus())
                .active(coupon.isActive())
                .build();
    }
}
