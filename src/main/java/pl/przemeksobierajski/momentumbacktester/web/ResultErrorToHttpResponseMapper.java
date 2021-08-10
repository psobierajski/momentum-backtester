package pl.przemeksobierajski.momentumbacktester.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.przemeksobierajski.momentumbacktester.momentum.ResultError;

class ResultErrorToHttpResponseMapper {

    static ResponseStatusException getExceptionFor(ResultError err) {
        return switch (err) {
            case SAMPLE_ERROR -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "A reason");
            default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Error");
        };
    }
}

