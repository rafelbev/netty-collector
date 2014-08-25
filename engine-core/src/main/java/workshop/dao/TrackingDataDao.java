package workshop.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import workshop.bo.TrackingData;

public interface TrackingDataDao extends PagingAndSortingRepository<TrackingData, String> {
}
