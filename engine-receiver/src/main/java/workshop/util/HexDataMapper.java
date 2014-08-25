package workshop.util;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Component;
import workshop.bo.HexData;
import workshop.dto.HexDataTO;

@Component
public class HexDataMapper {

    private BoundMapperFacade<HexData, HexDataTO> mapper;

    public HexDataMapper() {
        MapperFactory factory = new DefaultMapperFactory.Builder().build();
        factory.classMap(HexData.class, HexDataTO.class)
                .byDefault()
                .register();

        mapper = factory.getMapperFacade(HexData.class, HexDataTO.class, false);
    }

    public HexDataTO toDto(HexData hexData) {
        return mapper.map(hexData);
    }

    public HexData toBo(HexDataTO hexData) {
        return mapper.mapReverse(hexData);
    }
}
