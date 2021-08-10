package pl.przemeksobierajski.momentumbacktester.momentum;

import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pl.przemeksobierajski.momentumbacktester.financialdata.SecurityLoader;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
class Backtester {
    private final SecurityLoader securityLoader;

    public Backtester(@Qualifier("cachedFileBasedSecurityLoader") SecurityLoader securityLoader) {
        this.securityLoader = securityLoader;
    }

    // TODO: 10/08/2021 add error handling
    public Either<ResultError, Result> backtestMomentumFor(InputData inputData) {
        final List<Security> securities = securityLoader.getSecuritiesByTickers(inputData.getSecurityType(), inputData.getTickers());
        final Price startCashValue = Price.parse("10000.00");
        final YearMonth startOfPeriod = inputData.getFromAdjustedWithAvailableData(securities);
        final YearMonth endOfPeriod = inputData.getToAdjustedWithAvailableData(securities);
        final List<Result.Transaction> allTransactions = new LinkedList<>();
        final List<Result.NetAssetValue> historicalNetAssetValues = new LinkedList<>();

        Security bestPerformer = pickBest(securities, startOfPeriod, inputData.getMomentumPeriodInMonths());
        PortfolioState portfolioState = buyNewAsset(bestPerformer, startCashValue, startOfPeriod);
        YearMonth currentMonth = startOfPeriod;
        while (currentMonth.isBefore(endOfPeriod.plusMonths(1))) {
            portfolioState = portfolioState.updateCurrentMonth(currentMonth);
            bestPerformer = pickBest(securities, currentMonth, inputData.getMomentumPeriodInMonths());

            if (shouldRebalancePortfolio(portfolioState, bestPerformer)) {
                Security oldAssetInPortfolio = portfolioState.getAssetInPortfolio();
                portfolioState = sellCurrentAndBuyNewAsset(bestPerformer, portfolioState);
                allTransactions.add(createTransaction(oldAssetInPortfolio, portfolioState));
            }
            historicalNetAssetValues.add(createNetAssetValue(portfolioState));
            currentMonth = currentMonth.plusMonths(1);
        }

        return Either.right(new Result(
                extractTickers(securities),
                startCashValue, startOfPeriod,
                endOfPeriod,
                allTransactions,
                historicalNetAssetValues
        ));
    }

    private PortfolioState sellCurrentAndBuyNewAsset(Security bestPerformer, PortfolioState portfolioState) {
        Price cashValue = portfolioState.calculateCurrentCashValue();
        portfolioState = buyNewAsset(bestPerformer, cashValue, portfolioState.getCurrentMonth());
        return portfolioState;
    }

    private Result.Transaction createTransaction(Security soldAsset, PortfolioState portfolioState) {
        YearMonth currentMonth = portfolioState.getCurrentMonth();
        Security boughtAsset = portfolioState.getAssetInPortfolio();
        return new Result.Transaction(
                currentMonth,
                soldAsset.getTicker(), soldAsset.priceOn(currentMonth),
                boughtAsset.getTicker(), boughtAsset.priceOn(currentMonth)
        );
    }

    private PortfolioState buyNewAsset(Security assetToBuy, Price currentCashValue, YearMonth currentMonth) {
        BigDecimal volume = calculateVolume(currentCashValue, assetToBuy.priceOn(currentMonth));
        return new PortfolioState(assetToBuy, volume, currentMonth);
    }

    Result.NetAssetValue createNetAssetValue(PortfolioState portfolioState) {
        return new Result.NetAssetValue(portfolioState.getCurrentMonth(), portfolioState.calculateCurrentCashValue());
    }

    private List<String> extractTickers(List<Security> securities) {
        return securities.stream().map(Security::getTicker).toList();
    }

    private boolean shouldRebalancePortfolio(PortfolioState portfolioState, Security bestPerformer) {
        return !StringUtils.equals(bestPerformer.getTicker(), portfolioState.getAssetInPortfolio().getTicker());
    }

    private BigDecimal calculateVolume(Price cashValue, Price priceOfAsset) {
        return cashValue.getAmount().divide(priceOfAsset.getAmount(), RoundingMode.FLOOR);
    }

    private Security pickBest(List<Security> securities, YearMonth currentMonth, int momentumPeriod) {
        return securities.stream()
                .sorted(Comparator.comparing(s -> s.lastMonthsPerformance(currentMonth, momentumPeriod)))
                .collect(Collectors.toList())
                .get(securities.size() - 1);
    }

    @AllArgsConstructor
    @Getter
    private static class PortfolioState {
        private final Security assetInPortfolio;
        private final BigDecimal assetVolumeInPortfolio;
        private final YearMonth currentMonth;

        public Price calculateCurrentCashValue() {
            return assetInPortfolio.priceOn(currentMonth).multiplyBy(assetVolumeInPortfolio);
        }

        public PortfolioState updateCurrentMonth(YearMonth currentMonth) {
            return new PortfolioState(this.assetInPortfolio, this.assetVolumeInPortfolio, currentMonth);
        }
    }
}
