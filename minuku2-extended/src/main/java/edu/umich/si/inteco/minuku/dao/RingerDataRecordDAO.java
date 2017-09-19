package edu.umich.si.inteco.minuku.dao;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import edu.umich.si.inteco.minuku.model.DataRecord.RingerDataRecord;
import edu.umich.si.inteco.minukucore.dao.DAO;
import edu.umich.si.inteco.minukucore.dao.DAOException;
import edu.umich.si.inteco.minukucore.user.User;

/**
 * Created by Lawrence on 2017/9/19.
 */

public class RingerDataRecordDAO implements DAO<RingerDataRecord> {
    @Override
    public void setDevice(User user, UUID uuid) {

    }

    @Override
    public void add(RingerDataRecord entity) throws DAOException {

    }

    @Override
    public void delete(RingerDataRecord entity) throws DAOException {

    }

    @Override
    public Future<List<RingerDataRecord>> getAll() throws DAOException {
        return null;
    }

    @Override
    public Future<List<RingerDataRecord>> getLast(int N) throws DAOException {
        return null;
    }

    @Override
    public void update(RingerDataRecord oldEntity, RingerDataRecord newEntity) throws DAOException {

    }
}
