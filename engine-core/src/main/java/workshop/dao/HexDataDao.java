package workshop.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import workshop.bo.HexData;

public interface HexDataDao extends PagingAndSortingRepository<HexData, String> {
}
