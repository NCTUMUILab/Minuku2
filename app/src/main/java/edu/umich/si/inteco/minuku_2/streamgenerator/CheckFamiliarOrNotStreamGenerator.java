package edu.umich.si.inteco.minuku_2.streamgenerator;

import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import edu.umich.si.inteco.minuku.config.Constants;
import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku.manager.MinukuDAOManager;
import edu.umich.si.inteco.minuku.manager.MinukuStreamManager;
import edu.umich.si.inteco.minuku.streamgenerator.AndroidStreamGenerator;
import edu.umich.si.inteco.minuku_2.dao.CheckFamiliarOrNotDAO;
import edu.umich.si.inteco.minuku_2.model.CheckFamiliarOrNotDataRecord;
import edu.umich.si.inteco.minuku_2.service.CheckFamiliarOrNotService;
import edu.umich.si.inteco.minuku_2.stream.CheckFamiliarOrNotStream;
import edu.umich.si.inteco.minukucore.dao.DAOException;
import edu.umich.si.inteco.minukucore.exception.StreamAlreadyExistsException;
import edu.umich.si.inteco.minukucore.exception.StreamNotFoundException;
import edu.umich.si.inteco.minukucore.stream.Stream;

/**
 * Created by Lawrence on 2017/6/5.
 */

public class CheckFamiliarOrNotStreamGenerator extends AndroidStreamGenerator<CheckFamiliarOrNotDataRecord> {

    private CheckFamiliarOrNotStream mStream;
    protected String TAG = "CheckFamiliarOrNotStreamGenerator";

    private final ScheduledExecutorService mScheduledExecutorService;
    public static final int REFRESH_FREQUENCY = 1; //1s, 10000ms
    public static final int BACKGROUND_RECORDING_INITIAL_DELAY = 0;

    private float latitude;
    private float longitude;

    private int home = 0;
    private int neighbor = 0;
    private int outside = 0;

    Context mContext;

    private CheckFamiliarOrNotDAO mDAO;

    public CheckFamiliarOrNotStreamGenerator(){
        mScheduledExecutorService = Executors.newScheduledThreadPool(REFRESH_FREQUENCY);
    }

    public CheckFamiliarOrNotStreamGenerator(Context applicationContext){
        super(applicationContext);

        this.mContext = applicationContext;

        this.mStream = new CheckFamiliarOrNotStream(Constants.DEFAULT_QUEUE_SIZE);
        this.mDAO = MinukuDAOManager.getInstance().getDaoFor(CheckFamiliarOrNotDataRecord.class);

        mScheduledExecutorService = Executors.newScheduledThreadPool(REFRESH_FREQUENCY);

        this.register();
    }

    @Override
    public void register() {
        Log.d(TAG, "Registering with StreamManager.");
        try {
            MinukuStreamManager.getInstance().register(mStream, CheckFamiliarOrNotDataRecord.class, this);
        } catch (StreamNotFoundException streamNotFoundException) {
            Log.e(TAG, "One of the streams on which CheckFamiliarOrNotDataRecord depends in not found.");
        } catch (StreamAlreadyExistsException streamAlreadyExistsException) {
            Log.e(TAG, "Another stream which provides CheckFamiliarOrNotDataRecord is already registered.");
        }
    }

    @Override
    public void onStreamRegistration() {
        //
        Log.e(TAG,"onStreamRegistration");

        //startCheckFamiliarOrNotMainThread();
        if(!CheckFamiliarOrNotService.isServiceRunning()) {
            Intent intent = new Intent(mContext, CheckFamiliarOrNotService.class);
            mContext.startService(intent);
        }
//        PendingIntent.getService(mContext, 0, intent, 0); //startService

    }

    @Override
    public Stream<CheckFamiliarOrNotDataRecord> generateNewStream() {
        return mStream;
    }

    @Override
    public boolean updateStream() {

        Log.d(TAG, "updateStream called");
        //TODO get service data
        CheckFamiliarOrNotDataRecord checkFamiliarOrNotDataRecord =
                new CheckFamiliarOrNotDataRecord(home,neighbor,outside); //staticornot,
        mStream.add(checkFamiliarOrNotDataRecord);
        Log.d(TAG, "CheckFamiliarOrNot to be sent to event bus" + checkFamiliarOrNotDataRecord);
        // also post an event.
        EventBus.getDefault().post(checkFamiliarOrNotDataRecord);
        try {
            mDAO.add(checkFamiliarOrNotDataRecord);
        } catch (DAOException e) {
            e.printStackTrace();
            return false;
        }catch (NullPointerException e){ //Sometimes no data is normal
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public long getUpdateFrequency() {
        return 15;
    }

    @Override
    public void sendStateChangeEvent() {

    }

    public void setcheckFamiliarOrNotDataRecord(CheckFamiliarOrNotDataRecord checkFamiliarOrNotDataRecord){
        mDAO.insert(checkFamiliarOrNotDataRecord);


    }

    @Override
    public void offer(CheckFamiliarOrNotDataRecord dataRecord) {

    }
}
