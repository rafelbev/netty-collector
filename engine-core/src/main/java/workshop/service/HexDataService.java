package workshop.service;


import org.springframework.data.domain.Pageable;
import workshop.bo.HexData;

public interface HexDataService {

    public HexData load(String id);

    public HexData create(HexData data);

    public HexData update(HexData data);

    public void delete(String id);

    public void delete(HexData data);

    public Iterable<HexData> findAll(Pageable pageable);

}
