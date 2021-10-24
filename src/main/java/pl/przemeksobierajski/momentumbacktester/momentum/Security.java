package pl.przemeksobierajski.momentumbacktester.momentum;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Map;
import java.util.TreeMap;

import static java.util.stream.Collectors.toMap;

@Getter
@ToString
@EqualsAndHashCode
public class Security {
    private final String ticker;
    private final String description;
    @Getter(AccessLevel.NONE)
    private final Map<YearMonth, Price> priceHistory = new TreeMap<>();
    private final YearMonth dataFrom;
    private final YearMonth dataTo;

    public Security(
            String ticker,
            String description,
            Map<String, String> priceByMonthMap
    ) {
        this.ticker = ticker;
        this.description = description;
        this.priceHistory.putAll(transform(priceByMonthMap));
        this.dataFrom = priceHistory.keySet().stream()
                .findFirst()
                .orElse(YearMonth.now());
        this.dataTo = priceHistory.keySet().stream()
                .reduce((first, second) -> second)
                .orElse(YearMonth.now());
    }

    private Map<YearMonth, Price> transform(Map<String, String> priceByMonthMap) {
        return priceByMonthMap.entrySet().stream()
                .collect(toMap(e -> YearMonth.parse(e.getKey()), e -> Price.parse(e.getValue())));
    }

    public Price priceOn(YearMonth yearMonth) {
        return priceHistory.get(yearMonth);
    }

    public BigDecimal lastMonthsPerformance(YearMonth currentMonth, int momentumPeriod) {
        return priceHistory.get(currentMonth).getAmount()
                .divide(priceHistory.get(currentMonth.minusMonths(momentumPeriod)).getAmount(), RoundingMode.UP);
    }

}
