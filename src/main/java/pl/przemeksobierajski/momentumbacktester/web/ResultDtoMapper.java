package pl.przemeksobierajski.momentumbacktester.web;

import org.mapstruct.Mapper;
import pl.przemeksobierajski.momentumbacktester.momentum.Price;
import pl.przemeksobierajski.momentumbacktester.momentum.Result;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
interface ResultDtoMapper {

    ResultDto map(Result security);

    default BigDecimal map(Price value) {
        return value.getAmount();
    }
}
