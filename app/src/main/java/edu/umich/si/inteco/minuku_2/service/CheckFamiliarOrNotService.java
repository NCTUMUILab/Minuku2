package edu.umich.si.inteco.minuku_2.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import edu.umich.si.inteco.minuku_2.R;

import static edu.umich.si.inteco.minuku_2.MainActivity.task;

/**
 * Created by Lawrence on 2017/4/23.
 */

public class CheckFamiliarOrNotService extends Service {

    final private String LOG_TAG = "CheckFamiliarOrNotService";

    //public ContextManager mContextManager; //TODO might be removed for the new logic in this code.
    private static Context serviceInstance = null;
    private static Handler mMainThread;
    private Context mContext;
    Intent intent = new Intent();
    private ProgressDialog loadingProgressDialog;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public CheckFamiliarOrNotService(){}

    public void onCreate(){
        super.onCreate();
        Log.d("CheckFamiliarOrNotService", "onCreate");

        //mode = Mode.setMode(2);
        /*** determine what task is now ***/
        task = getString(R.string.current_task);
        Log.e(LOG_TAG,"Mode: "+task);

        serviceInstance = this;

        //mContextManager = new ContextManager(this);
        //ActivityRecognitionStreamGenerator.setContext(this);

    }

    public static Context setCheckFamiliarOrNotService(){
        return serviceInstance;
    }

    public static Context getInstance() {
        if(CheckFamiliarOrNotService.serviceInstance == null) {
            try {
                CheckFamiliarOrNotService.serviceInstance = new CheckFamiliarOrNotService();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return CheckFamiliarOrNotService.serviceInstance;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("CheckFamiliarOrNotService", "[test service running] going to start the probe service, isServiceRunning:  " + isServiceRunning());

        startService();

        return START_STICKY;
    }


    public void startService(){
        //mContextManager.startContextManager();
        //儲存新的contextual information
        runMainThread();

    }

    public static void runMainThread(){
        mMainThread = new Handler();

        /**start repeatedly store the extracted contextual information into Record objects**/

        mMainThread.post(mMainThreadrunnable);
    }

    static Runnable mMainThreadrunnable = new Runnable() {
        @Override
        public void run() {


        }
    };

    public static boolean isServiceRunning() {
        return serviceInstance != null;
    }
}
