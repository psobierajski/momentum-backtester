package pl.przemeksobierajski.momentumbacktester.momentum;

import lombok.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@ToString
@EqualsAndHashCode
public class Result {
    @Getter(AccessLevel.NONE)
    private final Set<String> testedTickers = new HashSet<>();
    private final BigDecimal amountInvestedInEur;
    private final YearMonth firstMonth;
    private final YearMonth lastMonth;
    @Getter(AccessLevel.NONE)
    private final List<Transaction> allTransactions = new ArrayList<>();
    @Getter(AccessLevel.NONE)
    private final List<NetAssetValue> historicalNetAssetValues = new ArrayList<>();

    public Result(
            List<String> tickers,
            Price startCashValue,
            YearMonth startOfPeriod,
            YearMonth endOfPeriod,
            List<Transaction> allTransactions,
            List<NetAssetValue> historicalNetAssetValues
    ) {
        this.testedTickers.addAll(tickers);
        this.amountInvestedInEur = startCashValue.getAmount();
        this.firstMonth = startOfPeriod;
        this.lastMonth = endOfPeriod;
        this.allTransactions.addAll(allTransactions);
        this.historicalNetAssetValues.addAll(historicalNetAssetValues);
    }

    public Price getNetAssetValueInEur() {
        if (historicalNetAssetValues.isEmpty()) {
            return Price.parse(amountInvestedInEur.toString());
        }
        return historicalNetAssetValues.get(historicalNetAssetValues.size() - 1).getValue();
    }

    public int getNoOfTransactions() {
        return allTransactions.size();
    }

    public List<NetAssetValue> getHistoricalNetAssetValues() {
        return List.copyOf(historicalNetAssetValues);
    }

    public Set<String> getTestedTickers() {
        return Set.copyOf(testedTickers);
    }

    public List<Transaction> getAllTransactions() {
        return List.copyOf(allTransactions);
    }

    @Getter
    @AllArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class Transaction {

        private final YearMonth date;
        private final String soldTicker;
        private final Price soldPrice;
        private final String boughtTicker;
        private final Price boughtPrice;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class NetAssetValue {
        private final YearMonth date;
        private final Price value;
    }
}
