package pl.przemeksobierajski.momentumbacktester.web;

import org.springframework.stereotype.Component;
import pl.przemeksobierajski.momentumbacktester.momentum.Result;
import pl.przemeksobierajski.momentumbacktester.momentum.Security;

import java.util.List;

@Component
class MapperFacade {
    private final SecurityDtoMapper securityDtoMapper;
    private final ResultDtoMapper resultDtoMapper;

    public MapperFacade(SecurityDtoMapper securityDtoMapper, ResultDtoMapper resultDtoMapper) {
        this.securityDtoMapper = securityDtoMapper;
        this.resultDtoMapper = resultDtoMapper;
    }

    List<SecurityDto> map(List<Security> securities) {
        return securityDtoMapper.map(securities);
    }

    ResultDto map(Result result) {
        return resultDtoMapper.map(result);
    }
}
