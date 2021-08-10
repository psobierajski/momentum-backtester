package pl.przemeksobierajski.momentumbacktester.web;

import lombok.Data;

import java.time.YearMonth;

@Data
class SecurityDto {
    private String description;
    private String ticker;
    private YearMonth dataFrom;
    private YearMonth dataTo;
}
