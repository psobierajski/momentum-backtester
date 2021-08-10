package pl.przemeksobierajski.momentumbacktester.financialdata.file;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Component;
import pl.przemeksobierajski.momentumbacktester.financialdata.SecurityLoader;
import pl.przemeksobierajski.momentumbacktester.momentum.SecurityType;
import pl.przemeksobierajski.momentumbacktester.momentum.Security;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Component("fileBasedSecurityLoader")
class FileBasedSecurityLoader implements SecurityLoader {
    private final DirectoryPathFactory directoryPathFactory;

    public FileBasedSecurityLoader(DirectoryPathFactory directoryPathFactory) {
        this.directoryPathFactory = directoryPathFactory;
    }

    @Override
    public List<Security> getAllSecurities(SecurityType securityType) {
        return loadAll(securityType);
    }

    private List<Security> loadAll(SecurityType securityType) {
        String path = directoryPathFactory.getPath(securityType);
        try (Stream<Path> stream = Files.list(Paths.get(path))) {
            return stream.filter(file -> !Files.isDirectory(file))
                    .map(this::loadFromFile)
                    .toList();
        } catch (IOException e) {
            // TODO: 09/08/2021 add proper logging across the board
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public List<Security> getSecuritiesByTickers(SecurityType securityType, List<String> tickers) {
        return loadAll(securityType)
                .stream()
                .filter(sec -> tickers.contains(sec.getTicker()))
                .toList();
    }

    private Security loadFromFile(Path filePath) {
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath.toString()))) {
            List<String[]> csvLines = csvReader.readAll();
            String description = csvLines.get(0)[1];
            Map<String, String> priceByMonthMap = csvLines.stream()
                    .skip(1)
                    .collect(toMap(line -> line[0], line -> line[1]));
            String ticker = filePath.getFileName().toString().replace(".csv", "");
            return new Security(ticker, description, priceByMonthMap);
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }
}
