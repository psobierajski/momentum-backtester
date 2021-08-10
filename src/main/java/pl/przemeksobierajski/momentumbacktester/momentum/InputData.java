package pl.przemeksobierajski.momentumbacktester.momentum;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
public class InputData {
    @Getter(AccessLevel.NONE)
    private final List<String> tickers = new ArrayList<>();
    private final SecurityType securityType;
    private final int momentumPeriodInMonths;
    @Getter(AccessLevel.NONE)
    private final YearMonth from;
    @Getter(AccessLevel.NONE)
    private final YearMonth to;

    public InputData(List<String> tickers, SecurityType securityType, int momentumPeriodInMonths, String from, String to) {
        this.tickers.addAll(tickers);
        this.securityType = securityType;
        this.momentumPeriodInMonths = momentumPeriodInMonths;
        this.from = Try.of(() -> YearMonth.parse(from)).getOrElse(() -> YearMonth.parse("1900-01"));
        this.to = Try.of(() -> YearMonth.parse(to)).getOrElse(YearMonth::now);
    }

    YearMonth getFromAdjustedWithAvailableData(List<Security> securities) {
        YearMonth fullDataAvailableFrom = securities.stream()
                .map(Security::getDataFrom)
                .max(YearMonth::compareTo)
                .orElse(YearMonth.now())
                .plusMonths(momentumPeriodInMonths);

        return from.isAfter(fullDataAvailableFrom) ? from : fullDataAvailableFrom;
    }

    YearMonth getToAdjustedWithAvailableData(List<Security> securities) {
        YearMonth fullDataAvailableTo = securities.stream()
                .map(Security::getDataTo)
                .min(YearMonth::compareTo)
                .orElse(YearMonth.parse("1900-01"));

        return to.isBefore(fullDataAvailableTo) ? to : fullDataAvailableTo;
    }

    List<String> getTickers() {
        return List.copyOf(tickers);
    }
}
