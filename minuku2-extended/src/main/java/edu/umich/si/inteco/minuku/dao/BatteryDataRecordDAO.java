package edu.umich.si.inteco.minuku.dao;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import edu.umich.si.inteco.minuku.model.DataRecord.BatteryDataRecord;
import edu.umich.si.inteco.minukucore.dao.DAO;
import edu.umich.si.inteco.minukucore.dao.DAOException;
import edu.umich.si.inteco.minukucore.user.User;

/**
 * Created by Lawrence on 2017/9/19.
 */

public class BatteryDataRecordDAO implements DAO<BatteryDataRecord> {
    @Override
    public void setDevice(User user, UUID uuid) {

    }

    @Override
    public void add(BatteryDataRecord entity) throws DAOException {

    }

    @Override
    public void delete(BatteryDataRecord entity) throws DAOException {

    }

    @Override
    public Future<List<BatteryDataRecord>> getAll() throws DAOException {
        return null;
    }

    @Override
    public Future<List<BatteryDataRecord>> getLast(int N) throws DAOException {
        return null;
    }

    @Override
    public void update(BatteryDataRecord oldEntity, BatteryDataRecord newEntity) throws DAOException {

    }
}
