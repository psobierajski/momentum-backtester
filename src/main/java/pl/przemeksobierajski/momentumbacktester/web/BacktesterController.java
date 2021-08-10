package pl.przemeksobierajski.momentumbacktester.web;

import io.vavr.control.Try;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.przemeksobierajski.momentumbacktester.chart.ChartDrawer;
import pl.przemeksobierajski.momentumbacktester.momentum.BacktesterWebFacade;
import pl.przemeksobierajski.momentumbacktester.momentum.Result;
import pl.przemeksobierajski.momentumbacktester.momentum.SecurityType;
import pl.przemeksobierajski.momentumbacktester.momentum.InputData;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/momentum/backtester")
class BacktesterController {

    private final BacktesterWebFacade backtesterFacade;
    private final MapperFacade mapperFacade;
    private final ChartDrawer chartDrawer;

    public BacktesterController(
            BacktesterWebFacade backtesterFacade,
            MapperFacade mapperFacade,
            ChartDrawer chartDrawer
    ) {
        this.backtesterFacade = backtesterFacade;
        this.mapperFacade = mapperFacade;
        this.chartDrawer = chartDrawer;
    }

    @GetMapping("/securities/{securityType}")
    List<SecurityDto> getAvailableSecurities(@PathVariable String securityType) {
        return mapperFacade.map(backtesterFacade.listAll(parse(securityType)));
    }

    private SecurityType parse(String securityType) {
        String clientErrorMessage = String.format(
                "There is no security type: %s. Available types: %s", securityType, Arrays.toString(SecurityType.values())
        );

        return Try.of(() -> SecurityType.valueOf(securityType.toUpperCase()))
                .getOrElseThrow(ex -> errorStatus(HttpStatus.BAD_REQUEST, clientErrorMessage, ex));
    }

    @GetMapping("/securities/{securityType}/tester")
    ResultDto testStrategy(
            @PathVariable String securityType,
            @RequestHeader("tickers") List<String> tickers,
            @RequestHeader("momentumMonths") int momentumMonths,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam(value = "chart", required = false) boolean drawChart
    ) throws IOException {
        Result result = backtesterFacade.testStrategy(
                new InputData(tickers, parse(securityType), momentumMonths, from, to)
        ).getOrElseThrow(ResultErrorToHttpResponseMapper::getExceptionFor);

        if (drawChart) {
            chartDrawer.createChart(result.getHistoricalNetAssetValues(), new File("target/chart.jpeg"));
        }
        return mapperFacade.map(result);
    }

    private ResponseStatusException errorStatus(HttpStatus httpStatus, String errorMessage, Throwable ex) {
        return new ResponseStatusException(httpStatus, errorMessage, ex);
    }
}
