package pl.przemeksobierajski.momentumbacktester.momentum;

import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pl.przemeksobierajski.momentumbacktester.financialdata.SecurityLoader;

import java.util.List;

@Component
public class BacktesterWebFacade {
    private final SecurityLoader securityLoader;
    private final Backtester backtester;

    public BacktesterWebFacade(
            @Qualifier("cachedFileBasedSecurityLoader")SecurityLoader securityLoader,
            Backtester backtester
    ) {
        this.securityLoader = securityLoader;
        this.backtester = backtester;
    }

    public List<Security> listAll(SecurityType securityType) {
        return securityLoader.getAllSecurities(securityType);
    }

    public Either<ResultError, Result> testStrategy(InputData inputData) {
        return backtester.backtestMomentumFor(inputData);
    }
}
