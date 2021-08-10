package pl.przemeksobierajski.momentumbacktester.momentum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResultError {
    SAMPLE_ERROR("The reason of the error");

    private String message;
}
