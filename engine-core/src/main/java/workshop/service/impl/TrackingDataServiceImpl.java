package workshop.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import workshop.bo.TrackingData;
import workshop.dao.TrackingDataDao;
import workshop.service.TrackingDataService;

@Service
public class TrackingDataServiceImpl implements TrackingDataService {

    @Autowired
    private TrackingDataDao trackingDataDao;

    @Override
    public TrackingData load(String id) {
        return trackingDataDao.findOne(id);
    }

    @Override
    public TrackingData create(TrackingData data) {
        return trackingDataDao.save(data);
    }

    @Override
    public TrackingData update(TrackingData data) {
        return trackingDataDao.save(data);
    }

    @Override
    public void delete(String id) {
        trackingDataDao.delete(id);
    }

    @Override
    public void delete(TrackingData data) {
        trackingDataDao.delete(data);
    }

    @Override
    public Iterable<TrackingData> findAll(Pageable pageable) {
        return trackingDataDao.findAll(pageable);
    }
}
