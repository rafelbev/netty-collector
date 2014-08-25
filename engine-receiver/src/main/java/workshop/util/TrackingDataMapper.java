package workshop.util;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.converter.builtin.DateToStringConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Component;
import workshop.bo.TrackingData;
import workshop.dto.TrackingDataTO;

@Component
public class TrackingDataMapper {

    private BoundMapperFacade<TrackingData, TrackingDataTO> mapper;

    public TrackingDataMapper() {
        MapperFactory factory = new DefaultMapperFactory.Builder().build();
        ConverterFactory converterFactory = factory.getConverterFactory();
        converterFactory.registerConverter("dateConverter", new DateToStringConverter("yyyy-MM-dd HH:mm:ss:0000"));

        factory.classMap(TrackingData.class, TrackingDataTO.class)
                .fieldMap("updateTime", "updatetime").converter("dateConverter").add()
                .byDefault()
                .register();

        mapper = factory.getMapperFacade(TrackingData.class, TrackingDataTO.class, false);
    }

    public TrackingDataTO toDto(TrackingData trackingData) {
        return mapper.map(trackingData);
    }

    public TrackingData toBo(TrackingDataTO trackingData) {
        return mapper.mapReverse(trackingData);
    }
}
