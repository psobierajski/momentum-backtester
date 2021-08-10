package pl.przemeksobierajski.momentumbacktester.web;

import org.mapstruct.Mapper;
import pl.przemeksobierajski.momentumbacktester.momentum.Security;

import java.util.List;

@Mapper(componentModel = "spring")
interface SecurityDtoMapper {

    SecurityDto map(Security security);

    List<SecurityDto> map(List<Security> security);
}
