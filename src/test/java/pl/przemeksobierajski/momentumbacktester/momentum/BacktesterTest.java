package pl.przemeksobierajski.momentumbacktester.momentum;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.przemeksobierajski.momentumbacktester.financialdata.SecurityLoader;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class BacktesterTest {

    private static final int momentumPeriod = 2;
    private static final String EIMI_TICKER = "EIMI.UK";
    private static final String CSPX_TICKER = "CSPX.UK";
    private static final Security EIMI_ETF = createSecurity(EIMI_TICKER, "MSCI Emerging Markets",
            Stream.of(new String[][]{
                    {"2019-11", "8"},
                    {"2019-12", "9"},
                    {"2020-01", "10"}, //+2 in the last 2 months (best performer) - should buy EIMI
                    {"2020-02", "12"}, //+3 (best performer)
                    {"2020-03", "13"}, //+3 (best performer)
                    {"2020-04", "16"}, //+4  (best performer)
                    {"2020-05", "13"}, //0
                    {"2020-06", "8"}, //-8
                    {"2020-07", "6"}, //-7
                    {"2020-08", "7"}, //-1
                    {"2020-09", "9"}, //+3 (best performer) - should sell CSPX and buy EIMI
                    {"2020-10", "12"}, //+5 (best performer)
                    {"2020-11", "14"}, //+5 (best performer)
                    {"2020-12", "18"} //+6 (best performer)
            }).collect(Collectors.toMap(data -> data[0], data -> data[1])));
    private static final Security CSPX_ETF = createSecurity(CSPX_TICKER, "S&P 500",
            Stream.of(new String[][]{
                    {"2019-11", "12"},
                    {"2019-12", "9"},
                    {"2020-01", "10"}, //-2 in the last 2 months
                    {"2020-02", "9"}, //+0
                    {"2020-03", "8"}, //-2
                    {"2020-04", "9"}, //+0
                    {"2020-05", "10"}, //+2  (best performer) - should sell EIMI and buy CSPX
                    {"2020-06", "12"}, //+3  (best performer)
                    {"2020-07", "15"}, //+5  (best performer)
                    {"2020-08", "14"}, //+2  (best performer)
                    {"2020-09", "12"}, //-3
                    {"2020-10", "9"}, //-5
                    {"2020-11", "10"}, //-2
                    {"2020-12", "8"} //-1
            }).collect(Collectors.toMap(data -> data[0], data -> data[1])));
    private final Offset<Double> DOUBLE_OFFSET = Offset.offset(0.00001);

    @Mock
    private SecurityLoader securityLoader;
    @InjectMocks
    private Backtester backtester;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(securityLoader.getSecuritiesByTickers(eq(SecurityType.ETF), eq(List.of(EIMI_TICKER))))
                .thenReturn(List.of(EIMI_ETF));
        when(securityLoader.getSecuritiesByTickers(eq(SecurityType.ETF), eq(List.of(CSPX_TICKER))))
                .thenReturn(List.of(CSPX_ETF));
        when(securityLoader.getSecuritiesByTickers(eq(SecurityType.ETF), eq(List.of(EIMI_TICKER, CSPX_TICKER))))
                .thenReturn(List.of(CSPX_ETF, EIMI_ETF));
    }

    @Test
    void sunnyDayScenario_checkTransactions() {
        //when
        Result result = backtester.backtestMomentumFor(
                new InputData(List.of("EIMI.UK", "CSPX.UK"), SecurityType.ETF, momentumPeriod, "2020-01", "2020-12")
        ).get();

        //then
        assertThat(result.getNoOfTransactions()).isEqualTo(2);

        Result.Transaction firstTransaction = result.getAllTransactions().get(0);
        checkTransaction(firstTransaction, YearMonth.parse("2020-05"), EIMI_TICKER, EIMI_ETF, CSPX_TICKER, CSPX_ETF);

        Result.Transaction secondTransaction = result.getAllTransactions().get(1);
        checkTransaction(secondTransaction, YearMonth.parse("2020-09"), CSPX_TICKER, CSPX_ETF, EIMI_TICKER, EIMI_ETF);
    }

    @Test
    void sunnyDayScenario_checkEndPortfolioValue() {
        //2020-01: Buy 1000 shares of EIMI for 10.0 EUR per share (Total value: 10000 EUR)
        //2020-05: Sell 1000 EIMI for 13.0 Euro per share and Buy 1300 CSPX for 10.0 EUR per share  (Total value: 13000 EUR)
        //2020-09: Sell 1300 CSPX for 12.0 Euro per share and Buy 1733.33 EIMI for 9.0 EUR per share  (Total value: 15600 EUR)
        //2020-12: EIMI price soared to 18.0 EUR per share, so the portfolio value is 31200 EUR
        double expectedEndAssetValue = 31200.0;

        Result result = backtester.backtestMomentumFor(
                new InputData(List.of("EIMI.UK", "CSPX.UK"), SecurityType.ETF, momentumPeriod, "2020-01", "2020-12")
        ).get();

        assertThat(result.getAmountInvestedInEur().doubleValue()).isCloseTo(10000.0, DOUBLE_OFFSET);
        assertThat(result.getNetAssetValueInEur().getAmount().doubleValue()).isCloseTo(expectedEndAssetValue, DOUBLE_OFFSET);
    }

    private void checkTransaction(Result.Transaction firstTransaction, YearMonth firstTransactionDate, String soldEtfTicker, Security soldEtf, String boughtEtfTicker, Security boughtEtf) {
        assertThat(firstTransaction.getDate()).isEqualTo(firstTransactionDate);
        assertThat(firstTransaction.getSoldTicker()).isEqualTo(soldEtfTicker);
        assertThat(firstTransaction.getSoldPrice()).isEqualTo(soldEtf.priceOn(firstTransactionDate));
        assertThat(firstTransaction.getBoughtTicker()).isEqualTo(boughtEtfTicker);
        assertThat(firstTransaction.getBoughtPrice()).isEqualTo(boughtEtf.priceOn(firstTransactionDate));
    }

    @Test
    void noTransactionsOnOneAsset() {
        Result result = backtester.backtestMomentumFor(
                new InputData(List.of("EIMI.UK"), SecurityType.ETF, momentumPeriod, "2020-01", "2020-12")
        ).get();

        assertThat(result.getNoOfTransactions()).isEqualTo(0);
        assertThat(result.getAmountInvestedInEur().doubleValue()).isCloseTo(10000.0, DOUBLE_OFFSET);
        assertThat(result.getNetAssetValueInEur().getAmount().doubleValue()).isCloseTo(18000.0, DOUBLE_OFFSET);
    }

    @Test
    void adjustTimeRangeToDataWhenNotProvided() {
        Result result = backtester.backtestMomentumFor(
                new InputData(List.of("EIMI.UK"), SecurityType.ETF, momentumPeriod, null, null)
        ).get();

        assertThat(result.getFirstMonth()).isEqualTo(YearMonth.parse("2020-01"));
        assertThat(result.getLastMonth()).isEqualTo(YearMonth.parse("2020-12"));
    }

    @Test
    void adjustTimeRangeWhenDataForProvidedIsUnavailable() {
        Result result = backtester.backtestMomentumFor(
                new InputData(List.of("EIMI.UK"), SecurityType.ETF, momentumPeriod, "1999-02", "2022-11")
        ).get();

        assertThat(result.getFirstMonth()).isEqualTo(YearMonth.parse("2020-01"));
        assertThat(result.getLastMonth()).isEqualTo(YearMonth.parse("2020-12"));
    }

    // TODO: 10/08/2021 Add edge test cases

    private static Security createSecurity(String ticker, String desc, Map<String, String> priceHistoryMap) {
        return new Security(ticker, desc, priceHistoryMap);
    }


}