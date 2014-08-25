package workshop.service;


import org.springframework.data.domain.Pageable;
import workshop.bo.TrackingData;

public interface TrackingDataService {

    public TrackingData load(String id);

    public TrackingData create(TrackingData data);

    public TrackingData update(TrackingData data);

    public void delete(String id);

    public void delete(TrackingData data);

    public Iterable<TrackingData> findAll(Pageable pageable);

}
