package edu.umich.si.inteco.minuku_2.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import edu.umich.si.inteco.minuku.DBHelper.DBHelper;
import edu.umich.si.inteco.minuku.manager.DBManager;
import edu.umich.si.inteco.minuku_2.model.CheckFamiliarOrNotDataRecord;
import edu.umich.si.inteco.minukucore.dao.DAO;
import edu.umich.si.inteco.minukucore.dao.DAOException;
import edu.umich.si.inteco.minukucore.user.User;

/**
 * Created by Lawrence on 2017/6/5.
 */

public class CheckFamiliarOrNotDAO implements DAO<CheckFamiliarOrNotDataRecord>{

    private DBHelper dBHelper;
    private Context mContext;
    //private String DATABASE_TABLE = "CheckFamiliarOrNot.db";

    public CheckFamiliarOrNotDAO(Context applicationContext){
        this.mContext = applicationContext;

        dBHelper = DBHelper.getInstance(applicationContext);
    }

    @Override
    public void setDevice(User user, UUID uuid) {

    }

    @Override
    public void add(CheckFamiliarOrNotDataRecord entity) throws DAOException {
        //TODO insert to SQLlite


    }

    public void insert(CheckFamiliarOrNotDataRecord entity) {

        ContentValues values = new ContentValues();

        try {
            SQLiteDatabase db = DBManager.getInstance().openDatabase();
            values.put(DBHelper.home_col, entity.getHome());
            values.put(DBHelper.neighbor_col, entity.getneighbor());
            values.put(DBHelper.outside_col, entity.getoutside());

            db.insert(DBHelper.checkFamiliarOrNot_table, null, values);
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
        finally {
            values.clear();
            DBManager.getInstance().closeDatabase(); // Closing database connection
        }
    }

    public void query_3_Condition(){
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor homeCursor = db.rawQuery("SELECT "+ DBHelper.home_col +" FROM "+ DBHelper.checkFamiliarOrNot_table, null);
        Cursor neighborCursor = db.rawQuery("SELECT "+ DBHelper.neighbor_col +" FROM "+ DBHelper.checkFamiliarOrNot_table, null);
        Cursor outsideCursor = db.rawQuery("SELECT "+ DBHelper.outside_col +" FROM "+ DBHelper.checkFamiliarOrNot_table, null);

        int homerow    = homeCursor.getCount();
        int homecol    = homeCursor.getColumnCount();
        int neighborrow= neighborCursor.getCount();
        int neighborcol= neighborCursor.getColumnCount();
        int outsiderow = outsideCursor.getCount();
        int outsidecol = outsideCursor.getColumnCount();



    }

    @Override
    public void delete(CheckFamiliarOrNotDataRecord entity) throws DAOException {

    }

    @Override
    public Future<List<CheckFamiliarOrNotDataRecord>> getAll() throws DAOException {
        return null;
    }

    @Override
    public Future<List<CheckFamiliarOrNotDataRecord>> getLast(int N) throws DAOException {
        return null;
    }

    @Override
    public void update(CheckFamiliarOrNotDataRecord oldEntity, CheckFamiliarOrNotDataRecord newEntity) throws DAOException {

    }
}
