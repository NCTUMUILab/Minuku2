package edu.umich.si.inteco.minuku_2.streamgenerator;

import android.content.Context;
import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.umich.si.inteco.minuku.config.Constants;
import edu.umich.si.inteco.minuku.event.DecrementLoadingProcessCountEvent;
import edu.umich.si.inteco.minuku.event.IncrementLoadingProcessCountEvent;
import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku.manager.MinukuDAOManager;
import edu.umich.si.inteco.minuku.manager.MinukuStreamManager;
import edu.umich.si.inteco.minuku.streamgenerator.AndroidStreamGenerator;
import edu.umich.si.inteco.minuku_2.dao.DiabetesLogDAO;
import edu.umich.si.inteco.minuku_2.model.DiabetesLogDataRecord;
import edu.umich.si.inteco.minuku_2.preferences.ApplicationConstants;
import edu.umich.si.inteco.minuku_2.stream.DiabetesLogStream;
import edu.umich.si.inteco.minukucore.dao.DAOException;
import edu.umich.si.inteco.minukucore.event.NoDataChangeEvent;
import edu.umich.si.inteco.minukucore.exception.StreamAlreadyExistsException;
import edu.umich.si.inteco.minukucore.exception.StreamNotFoundException;
import edu.umich.si.inteco.minukucore.model.DataRecord;
import edu.umich.si.inteco.minukucore.stream.Stream;

/**
 * Created by shriti on 10/8/16.
 */

public class DiabetesLogStreamGenerator extends AndroidStreamGenerator<DiabetesLogDataRecord> {

    private DiabetesLogStream mStream;
    protected String TAG = "DiabetesLogStreamGenerator";

    private DiabetesLogDAO mDAO;
    private Class<DiabetesLogDataRecord> diabetesLogDataRecordType;


    public DiabetesLogStreamGenerator() {

    }

    public DiabetesLogStreamGenerator(Context applicationContext, Class<DiabetesLogDataRecord> dataRecordType) {
        super(applicationContext);
        this.mStream = new DiabetesLogStream(Constants.DEFAULT_QUEUE_SIZE);
        this.mDAO = MinukuDAOManager.getInstance().getDaoFor(DiabetesLogDataRecord.class);
        this.diabetesLogDataRecordType = dataRecordType;
        this.register();
    }

    @Override
    public void register() {
        Log.d(TAG, "Registering with Stream Manager");
        try {
            MinukuStreamManager.getInstance().register(mStream, DiabetesLogDataRecord.class, this);
        } catch (StreamNotFoundException streamNotFoundException) {
            Log.e(TAG, "One of the streams on which DiabetesLogDataRecord/DiabetesLogStream depends in not found.");
        } catch (StreamAlreadyExistsException streamAlreadyExsistsException) {
            Log.e(TAG, "Another stream which provides DiabetesLogDataRecord/DiabetesLogStream is already registered.");
        }
    }

    @Override
    public Stream<DiabetesLogDataRecord> generateNewStream() {
        return mStream;
    }

    @Override
    public boolean updateStream() {
        MinukuStreamManager.getInstance().handleNoDataChangeEvent(
                new NoDataChangeEvent(diabetesLogDataRecordType));
        return true;    }

    //TODO: change from debug to the non debug time - 3 hours
    @Override
    public long getUpdateFrequency() {
        return ApplicationConstants.DIABETES_LOG_STREAM_GENERATOR_UPDATE_FREQUENCY_MINUTES;
    }

    @Override
    public void sendStateChangeEvent() {

    }

    @Override
    public void onStreamRegistration() {
        Log.d(TAG, "Stream " + TAG + " registered successfully");
        EventBus.getDefault().post(new IncrementLoadingProcessCountEvent());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try
                {
                    Log.d(TAG, "Stream " + TAG + "initialized from previous state");
                    Future<List<DiabetesLogDataRecord>> listFuture =
                            mDAO.getLast(Constants.DEFAULT_QUEUE_SIZE);
                    while(!listFuture.isDone()) {
                        Thread.sleep(1000);
                    }
                    Log.d(TAG, "Received data from Future for " + TAG);
                    mStream.addAll(new LinkedList<>(listFuture.get()));
                } catch (DAOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    EventBus.getDefault().post(new DecrementLoadingProcessCountEvent());
                }
            }
        });
    }

    @Override
    public void offer(DiabetesLogDataRecord dataRecord) {
        try {
            //add to stream
            mStream.add(dataRecord);
            //add to database
            mDAO.add(dataRecord);
        } catch (DAOException e){
            e.printStackTrace();
        }
    }
}
