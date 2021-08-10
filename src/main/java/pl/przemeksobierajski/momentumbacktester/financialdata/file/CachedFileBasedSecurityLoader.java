package pl.przemeksobierajski.momentumbacktester.financialdata.file;

import org.springframework.stereotype.Component;
import pl.przemeksobierajski.momentumbacktester.financialdata.SecurityLoader;
import pl.przemeksobierajski.momentumbacktester.momentum.Security;
import pl.przemeksobierajski.momentumbacktester.momentum.SecurityType;

import java.util.List;

@Component("cachedFileBasedSecurityLoader")
class CachedFileBasedSecurityLoader implements SecurityLoader {
    private final FileBasedSecurityLoader decoratedLoader;

    CachedFileBasedSecurityLoader(FileBasedSecurityLoader fileBasedSecurityLoader) {
        this.decoratedLoader = fileBasedSecurityLoader;
    }

    @Override
    public List<Security> getAllSecurities(SecurityType type) {
        // TODO: 09/08/2021 implement caching with Caffeine
        return decoratedLoader.getAllSecurities(type);
    }

    @Override
    public List<Security> getSecuritiesByTickers(SecurityType type, List<String> tickers) {
        // TODO: 09/08/2021 implement caching with Caffeine
        return decoratedLoader.getSecuritiesByTickers(type, tickers);
    }
}
