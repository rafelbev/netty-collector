package workshop.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import workshop.bo.HexData;
import workshop.dao.HexDataDao;
import workshop.service.HexDataService;

@Service
public class HexDataServiceImpl implements HexDataService {

    @Autowired
    private HexDataDao hexDataDao;

    @Override
    public HexData load(String id) {
        return hexDataDao.findOne(id);
    }

    @Override
    public HexData create(HexData data) {
        return hexDataDao.save(data);
    }

    @Override
    public HexData update(HexData data) {
        return hexDataDao.save(data);
    }

    @Override
    public void delete(String id) {
        hexDataDao.delete(id);
    }

    @Override
    public void delete(HexData data) {
        hexDataDao.delete(data);
    }

    @Override
    public Iterable<HexData> findAll(Pageable pageable) {
        return hexDataDao.findAll(pageable);
    }
}
