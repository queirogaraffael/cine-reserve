package com.example.cinema.api.domain.common;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Money {

    private BigDecimal amount;

    public Money(BigDecimal amount) {
        if (amount == null) {
            this.amount = BigDecimal.ZERO;
        } else {
            this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        }
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public Money add(Money other) {
        if (other == null) return this;
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        if (other == null) return this;
        return new Money(this.amount.subtract(other.amount));
    }

    public boolean isGreaterThan(Money other) {
        if (other == null) return true;
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        if (other == null) return true;
        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isLessThan(Money other) {
        if (other == null) return false;
        return this.amount.compareTo(other.amount) < 0;
    }
}
