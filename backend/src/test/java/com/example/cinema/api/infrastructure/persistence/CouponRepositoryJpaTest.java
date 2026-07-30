package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.coupon.Coupon;
import com.example.cinema.api.domain.coupon.CouponStatus;
import com.example.cinema.api.domain.coupon.DiscountType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CouponRepositoryJpaTest {

    @Autowired
    private CouponRepositoryJpa couponRepository;

    @Test
    void shouldSaveAndFindCouponByCode() {
        Coupon coupon = Coupon.builder()
                .code("BLACKFRIDAY")
                .description("Black Friday 50%")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(new BigDecimal("50.0"))
                .status(CouponStatus.AVAILABLE)
                .build();

        couponRepository.save(coupon);

        Optional<Coupon> found = couponRepository.findByCode("BLACKFRIDAY");
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Black Friday 50%");
    }

    @Autowired
    private UserRepositoryJpa userRepository;

    @Test
    void shouldFindOnlyActiveCoupons() {
        com.example.cinema.api.domain.user.User user = new com.example.cinema.api.domain.user.User(
                "Test", "test@test.com", "pass", "123456", java.time.LocalDate.now(), com.example.cinema.api.domain.user.UserRole.USER
        );
        userRepository.save(user);

        Coupon activeCoupon = Coupon.builder()
                .code("ACTIVE1")
                .description("Active")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(new BigDecimal("10.0"))
                .status(CouponStatus.AVAILABLE)
                .user(user)
                .build();
        activeCoupon.activate();
        couponRepository.save(activeCoupon);

        Coupon inactiveCoupon = Coupon.builder()
                .code("INACTIVE1")
                .description("Inactive")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(new BigDecimal("10.0"))
                .status(CouponStatus.AVAILABLE)
                .user(user)
                .build();
        inactiveCoupon.deactivate();
        couponRepository.save(inactiveCoupon);

        java.util.List<Coupon> found = couponRepository.findByUserIdAndStatusAndActiveTrue(user.getId(), CouponStatus.AVAILABLE);
        
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getCode()).isEqualTo("ACTIVE1");
    }
}
