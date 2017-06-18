package edu.umich.si.inteco.minuku.dao;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import edu.umich.si.inteco.minuku.model.ArmuroDataRecord;
import edu.umich.si.inteco.minukucore.dao.DAO;
import edu.umich.si.inteco.minukucore.dao.DAOException;
import edu.umich.si.inteco.minukucore.user.User;

/**
 * Created by Lawrence on 2017/6/18.
 */

public class ArmuroDAO implements DAO<ArmuroDataRecord> {
    @Override
    public void setDevice(User user, UUID uuid) {

    }

    @Override
    public void add(ArmuroDataRecord entity) throws DAOException {

    }

    @Override
    public void delete(ArmuroDataRecord entity) throws DAOException {

    }

    @Override
    public Future<List<ArmuroDataRecord>> getAll() throws DAOException {
        return null;
    }

    @Override
    public Future<List<ArmuroDataRecord>> getLast(int N) throws DAOException {
        return null;
    }

    @Override
    public void update(ArmuroDataRecord oldEntity, ArmuroDataRecord newEntity) throws DAOException {

    }
}
