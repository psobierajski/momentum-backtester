package pl.przemeksobierajski.momentumbacktester.financialdata.file;

import org.springframework.stereotype.Component;
import pl.przemeksobierajski.momentumbacktester.momentum.SecurityType;

@Component
class DirectoryPathFactory {

    String getPath(SecurityType securityType) {
        return switch (securityType) {
            case ETF -> "src/main/resources/data/etf/";
            default -> throw new UnsupportedOperationException(String.format("%s not supported", securityType));
        };
    }
}
