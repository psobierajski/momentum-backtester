package pl.przemeksobierajski.momentumbacktester.web;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Period;
import java.time.YearMonth;
import java.util.Set;

@Data
class ResultDto {
    private Set<String> testedTickers;
    private BigDecimal amountInvestedInEur;
    private YearMonth firstMonth;
    private YearMonth lastMonth;
    private int noOfTransactions;
    private BigDecimal netAssetValueInEur;

    /**
     * Calculate the compound annual growth rate.
     * @return calculated CAGR.
     */
    public double getCagr() {
        long months = Period.between(firstMonth.atEndOfMonth(), lastMonth.atEndOfMonth()).toTotalMonths();
        double years = months / 12.0;
        return 100 * (Math.pow(getNetAssetValueInEur().divide(amountInvestedInEur, RoundingMode.FLOOR).doubleValue(), 1.0 / years) - 1.0);
    }
}
