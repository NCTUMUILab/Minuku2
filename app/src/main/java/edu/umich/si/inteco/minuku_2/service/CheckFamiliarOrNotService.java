package edu.umich.si.inteco.minuku_2.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.umich.si.inteco.minuku.config.Constants;
import edu.umich.si.inteco.minuku.manager.MinukuStreamManager;
import edu.umich.si.inteco.minuku.streamgenerator.LocationStreamGenerator;
import edu.umich.si.inteco.minuku.streamgenerator.TransportationModeStreamGenerator;
import edu.umich.si.inteco.minuku_2.R;
import edu.umich.si.inteco.minuku_2.model.CheckFamiliarOrNotDataRecord;
import edu.umich.si.inteco.minuku_2.streamgenerator.CheckFamiliarOrNotStreamGenerator;
import edu.umich.si.inteco.minukucore.exception.StreamNotFoundException;

import static edu.umich.si.inteco.minuku_2.MainActivity.task;

/**
 * Created by Lawrence on 2017/4/23.
 */

public class CheckFamiliarOrNotService extends Service {

    final private String TAG = "CheckFamiliarOrNotService";

    //public ContextManager mContextManager; //TODO might be removed for the new logic in this code.
    private static Context serviceInstance = null;
    private static Handler mMainThread;
    private Context mContext;
    Intent intent = new Intent();
    private ProgressDialog loadingProgressDialog;

    private ScheduledExecutorService mScheduledExecutorService;
    public static final int REFRESH_FREQUENCY = 5; //10s, 10000ms
    public static final int BACKGROUND_RECORDING_INITIAL_DELAY = 0;

    private float latitude;
    private float longitude;

    private int userid;

    private int home = 0;
    private int neighbor = 0;
    private int outside = 0;
    private float dist=0;

    private long nowtime=0;
    private long lastTimeSend_HomeMove=0;
    private long lastTimeSend_HomeNotMove=0;
    private long lastTimeSend_NearHomeMove=0;
    private long lastTimeSend_NearHomeNotMove=0;
    private long lastTimeSend_FarawayMove=0;
    private long lastTimeSend_FarawayNotMove=0;

    private float diffTime_HomeMove=0;
    private float diffTime_HomeNotMove=0;
    private float diffTime_NearHomeMove=0;
    private float diffTime_NearHomeNotMove=0;
    private float diffTime_FarawayMove=0;
    private float diffTime_FarawayNotMove=0;

    final private double period_HomeMove = 1;  //0.1 = 6min, 1 = 60min
    final private double period_HomeNotMove = 1;
    final private double period_NearHomeMove = 0.5; //0.5
    final private double period_NearHomeNotMove = 0.5;
    final private double period_FarawayMove = 0.5;
    final private double period_FarawayNotMove = 0.5;

    private int daily_count_HomeMove = 0;
    private int daily_count_HomeNotMove = 0;
    private int daily_count_NearHomeMove = 0;
    private int daily_count_NearHomeNotMove = 0;
    private int daily_count_FarawayMove = 0;
    private int daily_count_FarawayNotMove = 0;

    private String today;
    private String lastTimeSend_today = "NA";

    private int notifyID=1;
    private int qua_notifyID = 2;

    private String transportation;

    private boolean testingserver = false;

    private final String link = "https://qtrial2017q2az1.az1.qualtrics.com/jfe/form/SV_0VA9kDhoEeWHuYd";
    private String NotificationText = "Please click to fill the questionnaire";

    public static CheckFamiliarOrNotStreamGenerator checkFamiliarOrNotStreamGenerator;

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
        //Log.e(TAG,"Mode: "+task);

        serviceInstance = this;

        mContext = this;

        mScheduledExecutorService = Executors.newScheduledThreadPool(REFRESH_FREQUENCY);

        try {
            checkFamiliarOrNotStreamGenerator = (CheckFamiliarOrNotStreamGenerator) MinukuStreamManager.getInstance().getStreamGeneratorFor(CheckFamiliarOrNotDataRecord.class);
        }catch(StreamNotFoundException e){
            Log.e(TAG,"checkFamiliarOrNotStreamGenerator haven't created yet.");
        }
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

        runMainThread();

    }

    public void runMainThread(){
        mScheduledExecutorService.scheduleAtFixedRate(
                CheckFamiliarOrNotRunnable,
                BACKGROUND_RECORDING_INITIAL_DELAY,
                REFRESH_FREQUENCY,
                TimeUnit.SECONDS);
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }


    public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    Runnable CheckFamiliarOrNotRunnable = new Runnable() {
        @Override
        public void run() {

            /*  Example
            String _mcog = "http://mcog.asc.ohio-state.edu/apps/pip?lon=120.990079&lat=24.784819&userid=6&format=json";
            try{
                JSONObject json = readJsonFromUrl(_mcog);
                edu.umich.si.inteco.minuku.logger.Log.d(TAG,json.toString());

                edu.umich.si.inteco.minuku.logger.Log.e(TAG,"inhome : "+json.get("inhome")+" inhrsd : "+json.get("inhrsd")+" outhr : "+json.get("outhr"));

            }catch (JSONException e){
                edu.umich.si.inteco.minuku.logger.Log.e(TAG,"JSONException");
                e.printStackTrace();
            }catch (IOException e){
                edu.umich.si.inteco.minuku.logger.Log.e(TAG,"IOException");
                e.printStackTrace();
            }*/

            Log.d(TAG,"CheckFamiliarOrNotRunnable");

            Date curDate = new Date(System.currentTimeMillis()) ;
            String dateformat = "yyyyMMdd";
            SimpleDateFormat df = new SimpleDateFormat(dateformat);
            today = df.format(curDate);
            Log.d(TAG,"today is "+ today);

            //initialize
            home=0;
            neighbor=0;
            outside=0;
            dist=0;
            testingserver = false;

            if(!lastTimeSend_today.equals(today)){
                lastTimeSend_today = today;

                daily_count_HomeMove = 0;
                daily_count_HomeNotMove = 0;
                daily_count_NearHomeMove = 0;
                daily_count_NearHomeNotMove = 0;
                daily_count_FarawayMove = 0;
                daily_count_FarawayNotMove = 0;
            }


            //get latitude and longitude from LocationStreamGenerator
            try {
                latitude = LocationStreamGenerator.toCheckFamiliarOrNotLocationDataRecord.getLatitude();
                longitude = LocationStreamGenerator.toCheckFamiliarOrNotLocationDataRecord.getLongitude();
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.d(TAG+" testing server","latitude : "+latitude+" longitude : "+longitude);

            userid = 6;

            Log.d(TAG,"epoch time : "+String.valueOf((getCurrentTimeInMillis()/1000)));

            //check the server
            String mcog = "http://mcog.asc.ohio-state.edu/apps/pip?" +
                    "lon="+String.valueOf(longitude) +
                    "&lat="+String.valueOf(latitude) +
                    "&userid=" + String.valueOf(userid) + //TODO set userid to global variable.
                    "&tsphone=" + String.valueOf((getCurrentTimeInMillis()/1000)) +
//                    "&pdop=10.2" +
                    "&format=json";

            Log.d(TAG,mcog);

            try{
                JSONObject json = readJsonFromUrl(mcog);
                Log.d(TAG,json.toString());

                Log.d(TAG,"inhome : "+json.get("inhome")+" distance : "+json.get("distance"));

                home = Integer.valueOf(json.get("inhome").toString());
                if(json.get("distance").toString().contains("E"))
                    dist = 9999;
                else
                    dist = Float.valueOf(json.get("distance").toString());
                //neighbor = Integer.valueOf((String) json.get("inhrsd"));
                //outside = Integer.valueOf((String) json.get("outhr"));

                Log.d(TAG,"inhome : "+home+" distance : "+dist);

            }catch (JSONException e){
                Log.d(TAG,"JSONException");

                e.printStackTrace();
            }catch (IOException e){
                Log.d(TAG,"IOException");
                testingserver = true;
                e.printStackTrace();
            }

            Log.d(TAG,"Json runnable");
            /*
            * {"userid": 6, "inhome": 1, "outhr": 0, "tsphone": 9999, "ts": 1499762685,
               "polygonid": 5, "distance": 0, "tsentstate": 9999, "prevtsphone": 999999, "timeinstate": 0}
            * */

           /* //randomize where the user places is. 60% 30% 10%
            Random rand = new Random();
            int num = rand.nextInt(100) + 1;

            Log.d(TAG,"num : " + String.valueOf(num));
            if(num>=1&&num<=60)
                home=1;
            else if(num>=61&&num<=90)
                neighbor=1;
            else if(num>=91&&num<=100)
                outside=1;*/

/*
            try {
                transportation = TransportationModeStreamGenerator.toCheckFamiliarOrNotTransportationModeDataRecord.getConfirmedActivityType();
                if (transportation.equals("static"))
                    staticornot = 1;
                else
                    staticornot = 0;
            }catch (Exception e){
                Log.e(TAG,"No TransportationMode, yet.");
                e.printStackTrace();
            }
*/

            /*CheckFamiliarOrNotDataRecord checkFamiliarOrNotDataRecord =
                    new CheckFamiliarOrNotDataRecord(home,neighbor,outside);*/

            if(testingserver){ //TODO check data on sqllite and server is working or not.



            }else{

                triggerQualtrics();

                //checkFamiliarOrNotStreamGenerator.setDAO(checkFamiliarOrNotDataRecord);
                //mDAO.insert(checkFamiliarOrNotDataRecord);

            }

            showTransportationAndIsHome();

        }
    };

    private void triggerQualtrics(){

        Log.e(TAG,"triggerQualtrics");

        int homeorfaraway=0;
        if(home==1)
            homeorfaraway = 1;
        else if(home!=1 && dist<=200)
            homeorfaraway = 2;
        else if(home!=1 && dist>200)
            homeorfaraway = 3;

        try {
            transportation = TransportationModeStreamGenerator.toCheckFamiliarOrNotTransportationModeDataRecord.getConfirmedActivityType();
        }catch (Exception e){
            Log.e(TAG,"No TransportationMode, yet.");
            e.printStackTrace();
        }

        determineByContextQualtrics(homeorfaraway,transportation);

    }

    public void determineByContextQualtrics(int homeorfaraway, String transportation){

        String notiText = "Context is not existed."; //by default
        nowtime = getCurrentTimeInMillis();

        //Log.d(TAG,"nowtime:"+nowtime);

        if(homeorfaraway==1&&!transportation.equals("static")) {
            notiText = "Trigger for home, moving";

            diffTime_HomeMove = (float)((nowtime - lastTimeSend_HomeMove)/(60*60*1000.0));

            if(diffTime_HomeMove > period_HomeMove) {

                Log.d(TAG, notiText);

                Random rand = new Random();
                int num = rand.nextInt(100) + 1;
                //15% to trigger  //TODO temporialy add 20% for triggering
                if (num >= 1 && num <= 35) {
                    notiQualtrics(notiText);
                    daily_count_HomeMove++;
                }
                lastTimeSend_HomeMove = getCurrentTimeInMillis();

            }
        }
        else if(homeorfaraway==1&&transportation.equals("static")) {
            notiText = "Trigger for home, not moving";

            diffTime_HomeNotMove = (float)((nowtime - lastTimeSend_HomeNotMove)/(60*60*1000.0));

            if(diffTime_HomeNotMove > period_HomeNotMove) {

                Log.d(TAG, notiText);

                Random rand = new Random();
                int num = rand.nextInt(10) + 1;
                //10% to trigger //TODO temporialy add 20% for triggering
//                if (num == 1)
                if(num>=1 && num<=3) {
                    notiQualtrics(notiText);
                    daily_count_HomeNotMove++;
                }
                lastTimeSend_HomeNotMove = getCurrentTimeInMillis();

            }
        }
        else if(homeorfaraway==2&&!transportation.equals("static")) {
            notiText = "Trigger for near home, moving";


            diffTime_NearHomeMove = (float)((nowtime - lastTimeSend_NearHomeMove)/(60*60*1000.0));

            if(diffTime_NearHomeMove > period_NearHomeMove) {

                Log.d(TAG, notiText);

                Random rand = new Random();
                int num = rand.nextInt(10) + 1;
                //30% to trigger //TODO temporialy add 20% for triggering
                if (num >= 1 && num <= 5) {
                    notiQualtrics(notiText);
                    daily_count_NearHomeMove++;
                }
                lastTimeSend_NearHomeMove = getCurrentTimeInMillis();

            }
        }
        else if(homeorfaraway==2&&transportation.equals("static")) {
            notiText = "Trigger for near home, not moving";

            diffTime_NearHomeNotMove = (float)((nowtime - lastTimeSend_NearHomeNotMove)/(60*60*1000.0));

            if(diffTime_NearHomeNotMove > period_NearHomeNotMove) {

                Log.d(TAG, notiText);

                Random rand = new Random();
                int num = rand.nextInt(10) + 1;
                //30% to trigger //TODO temporialy add 20% for triggering
                if (num >= 1 && num <= 5) {
                    notiQualtrics(notiText);
                    daily_count_NearHomeNotMove++;
                }
                lastTimeSend_NearHomeNotMove = getCurrentTimeInMillis();

            }
        }
        else if(homeorfaraway==3&&!transportation.equals("static")) {
            notiText = "Trigger for faraway, moving";

            diffTime_FarawayMove = (float)((nowtime - lastTimeSend_FarawayMove)/(60*60*1000.0));

            if(diffTime_FarawayMove > period_FarawayMove) {

                Log.d(TAG, notiText);

                Random rand = new Random();
                int num = rand.nextInt(10) + 1;
                //50% to trigger //TODO temporialy add 20% for triggering
//                if (num == 1)
                if (num >= 1 && num <= 7) {
                    notiQualtrics(notiText);
                    daily_count_FarawayMove++;
                }
                lastTimeSend_FarawayMove = getCurrentTimeInMillis();

            }
        }
        else if(homeorfaraway==3&&transportation.equals("static")) {
            notiText = "Trigger for faraway, not moving";

            diffTime_FarawayNotMove = (float)((nowtime - lastTimeSend_FarawayNotMove)/(60*60*1000.0));

            if(diffTime_FarawayNotMove > period_FarawayNotMove) {

                Log.d(TAG, notiText);

                Random rand = new Random();
                int num = rand.nextInt(2) + 1;
                //50% to trigger //TODO temporialy add 20% for triggering
//                if (num == 1)
                if (num >= 1 && num <= 7) {
                    notiQualtrics(notiText);
                    daily_count_FarawayNotMove++;
                }
                lastTimeSend_FarawayNotMove = getCurrentTimeInMillis();

            }

        }

        //Log.d(TAG,notiText);



        // need to roll
        // notiQualtrics(notiText);

    }

    private void notiQualtrics(String notiText){

        Log.d(TAG,"notiQualtrics");

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);//Context.

        final Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        bigTextStyle.setBigContentTitle("Minuku2");
        bigTextStyle.bigText(notiText);

        Notification.Builder note = new Notification.Builder(mContext)
                .setContentTitle(Constants.APP_NAME)
                .setContentText(notiText)
                .setStyle(bigTextStyle)
                .setSmallIcon(R.drawable.self_reflection)
                .setAutoCancel(true);
        //note.flags |= Notification.FLAG_NO_CLEAR;
        //startForeground( 42, note );

        // pending implicit intent to view url
        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        resultIntent.setData(Uri.parse(link));

        PendingIntent pending = PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        note.setContentIntent(pending);

        // using the same tag and Id causes the new notification to replace an existing one
        mNotificationManager.notify(qua_notifyID, note.build()); //String.valueOf(System.currentTimeMillis()),
        note.build().flags = Notification.FLAG_AUTO_CANCEL;

    }

    private void showTransportationAndIsHome(){

        Log.e(TAG,"showTransportationAndIsHome");

        String inhomeornot="not working yet";

        if(home==1)
            inhomeornot = "home";
        else if(home!=1 && dist<=200)
            inhomeornot = "near home";
        else if(home!=1 && dist>200)
            inhomeornot = "faraway";
        try {
            String local_transportation = TransportationModeStreamGenerator.toCheckFamiliarOrNotTransportationModeDataRecord.getConfirmedActivityType();

            NotificationText = "Current Transportation Mode: " + local_transportation
                    + "\r\n" + "Is_home: " + inhomeornot

                    + "\r\n" + "Home, moving: " + daily_count_HomeMove
                    + "\r\n" + "Home, not moving: " + daily_count_HomeNotMove
                    + "\r\n" + "Near home, moving: " + daily_count_NearHomeMove
                    + "\r\n" + "Near home, not moving: " + daily_count_NearHomeNotMove
                    + "\r\n" + "Faraway, moving: " + daily_count_FarawayMove
                    + "\r\n" + "Faraway, not moving: " + daily_count_FarawayNotMove
                    + "\r\n" + "Total: " + (daily_count_HomeMove + daily_count_HomeNotMove
                                          + daily_count_NearHomeMove + daily_count_NearHomeNotMove
                                          + daily_count_FarawayMove + daily_count_FarawayNotMove);

            if(testingserver)
                NotificationText = NotificationText + "\r\n" + "Server Error";

            Log.e(TAG, "getConfirmedActivityType : " + local_transportation);
        }catch (Exception e){
            e.printStackTrace();
        }

        // pending implicit intent to view url
        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        resultIntent.setData(Uri.parse(link));
        PendingIntent pending = PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);//Context.
        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        bigTextStyle.setBigContentTitle("Minuku2");
        bigTextStyle.bigText(NotificationText);

        Notification note = new Notification.Builder(mContext)
                .setContentTitle(Constants.APP_NAME)
                .setContentText(NotificationText)
                .setContentIntent(pending)
                .setStyle(bigTextStyle)
                .setSmallIcon(R.drawable.self_reflection)
                .setAutoCancel(true)
                .build();
        //note.flags |= Notification.FLAG_NO_CLEAR;
        //startForeground( 42, note );

        // using the same tag and Id causes the new notification to replace an existing one
        mNotificationManager.notify(notifyID, note); //String.valueOf(System.currentTimeMillis()),
        note.flags = Notification.FLAG_AUTO_CANCEL;

        //note.setContentText(NotificationText);
        //mNotificationManager.notify(String.valueOf(System.currentTimeMillis()), 1, note.build());

    }

    public static boolean isServiceRunning() {
        return serviceInstance != null;
    }

    public long getCurrentTimeInMillis(){
        //get timzone
        TimeZone tz = TimeZone.getDefault();
        java.util.Calendar cal = java.util.Calendar.getInstance(tz);
        long t = cal.getTimeInMillis();
        return t;
    }
}
