package pl.przemeksobierajski.momentumbacktester.momentum;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

@ToString
@EqualsAndHashCode
public class Price {
    @Getter
    private final BigDecimal amount;

    private Price(BigDecimal amount) {
        this.amount = amount.setScale(6, RoundingMode.FLOOR);
    }

    public static Price parse(String price) {
        return new Price(new BigDecimal(price));
    }

    public Price multiplyBy(BigDecimal multiplicand) {
        return new Price(amount.multiply(multiplicand));
    }
}
