package edu.umich.si.inteco.minuku_2.DBHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.umich.si.inteco.minuku_2.manager.DBManager;

/**
 * Created by Lawrence on 2017/6/5.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String id = "_id";
    public static final String TAG = "DBHelper";
    public static final String home_col = "home";
    public static final String neighbor_col = "neighbor";
    public static final String outside_col = "outside";
    public static final String homeorfaraway = "homeorfaraway";
    public static final String staticornot = "staticornot";
    public static String DEVICE = "device_id";
    public static String TIME = "timeToSQLite";

    public static String checkFamiliarOrNot_table = "CheckFamiliarOrNot";

    public static String DATABASE_NAME = "MySQLite.db";
    public static int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        initiateDBManager();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("db","oncreate");
        createCheckFamiliarOrNotTable(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void initiateDBManager() {
        DBManager.initializeInstance(this);
    }

    public void createCheckFamiliarOrNotTable(SQLiteDatabase db){
        Log.d(TAG,"create noti table");

        String cmd = "CREATE TABLE " +
                checkFamiliarOrNot_table + "(" +
                id+" INTEGER PRIMARY KEY NOT NULL, " +
                DEVICE+" TEXT,"+
                TIME + " TEXT NOT NULL," +
                staticornot+" INTEGER,"+
                home_col +" INTEGER, " +
                neighbor_col + " INTEGER, " +
                outside_col +" INTEGER" +
                ");";

        db.execSQL(cmd);

    }

}
