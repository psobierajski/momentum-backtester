package pl.przemeksobierajski.momentumbacktester.financialdata;

import pl.przemeksobierajski.momentumbacktester.momentum.Security;
import pl.przemeksobierajski.momentumbacktester.momentum.SecurityType;

import java.util.List;

public interface SecurityLoader {
    List<Security> getAllSecurities(SecurityType type);

    List<Security> getSecuritiesByTickers(SecurityType type, List<String> tickers);
}
