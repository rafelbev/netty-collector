package workshop.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import workshop.bo.HexData;
import workshop.bo.TrackingData;
import workshop.dto.HexDataTO;
import workshop.dto.TrackingDataTO;
import workshop.service.HexDataService;
import workshop.service.TrackingDataService;
import workshop.util.HexDataMapper;
import workshop.util.TrackingDataMapper;

import java.io.Serializable;

public class Receiver {

    Logger logger = LoggerFactory.getLogger(Receiver.class);

    @Autowired
    private TrackingDataMapper trackingDataMapper;

    @Autowired
    private HexDataMapper hexDataMapper;

    @Autowired
    private TrackingDataService trackingDataService;

    @Autowired
    private HexDataService hexDataService;

    public void receiveTrackingData(Serializable data) {
        logger.debug("Received [{}]", data.toString());

        if (data instanceof TrackingDataTO) {
            TrackingData trackingData = trackingDataMapper.toBo((TrackingDataTO) data);
            trackingData = trackingDataService.create(trackingData);
            logger.info("Persisted trackingData on {} with Id: {}", trackingData.getTimestamp(), trackingData.getId());
        }

        if (data instanceof HexDataTO) {
            HexData hexData = hexDataMapper.toBo((HexDataTO) data);
            hexData = hexDataService.create(hexData);
            logger.info("Persisted trackingData {} with Id: {}", hexData.getTimestamp(), hexData.getId());
        }
    }

}
