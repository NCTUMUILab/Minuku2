package edu.umich.si.inteco.minuku_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.umich.si.inteco.minuku.DBHelper.DBHelper;
import edu.umich.si.inteco.minuku.service.TransportationModeService;
import edu.umich.si.inteco.minuku_2.service.BackgroundService;
import edu.umich.si.inteco.minuku_2.service.CheckFamiliarOrNotService;

/**
 * Created by Lawrence on 2017/7/19.
 */

public class BootCompleteReceiver extends BroadcastReceiver {

    private static final String TAG = "BootCompleteReceiver";
    private  static DBHelper dbhelper = null;


    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
        {
            Log.d(TAG,"boot_complete in first");

            try{
                dbhelper = new DBHelper(context);
                dbhelper.getWritableDatabase();
                Log.d(TAG,"db is ok");

                /*if(!InstanceManager.isInitialized()) {
                    InstanceManager.getInstance(context);
                }*/

            }finally {

                Log.d(TAG, "Successfully receive reboot request");

                //here we start the service
                Intent cintent = new Intent(context, CheckFamiliarOrNotService.class);
                context.startService(cintent);
                Log.d(TAG,"CheckFamiliarOrNotService is ok");

                Intent tintent = new Intent(context, TransportationModeService.class);
                context.startService(tintent);
                Log.d(TAG,"TransportationModeService is ok");

                Intent bintent = new Intent(context, BackgroundService.class);
                context.startService(bintent);
                Log.d(TAG,"BackgroundService is ok");

            }




        }

    }
}
