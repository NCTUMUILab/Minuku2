package edu.umich.si.inteco.minuku_2.streamgenerator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.umich.si.inteco.minuku.config.Constants;
import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku.manager.MinukuDAOManager;
import edu.umich.si.inteco.minuku.manager.MinukuStreamManager;
import edu.umich.si.inteco.minuku.streamgenerator.AndroidStreamGenerator;
import edu.umich.si.inteco.minuku.streamgenerator.TransportationModeStreamGenerator;
import edu.umich.si.inteco.minuku_2.DBHelper.DBHelper;
import edu.umich.si.inteco.minuku_2.R;
import edu.umich.si.inteco.minuku_2.dao.CheckFamiliarOrNotDAO;
import edu.umich.si.inteco.minuku_2.manager.DBManager;
import edu.umich.si.inteco.minuku_2.model.CheckFamiliarOrNotDataRecord;
import edu.umich.si.inteco.minuku_2.service.CheckFamiliarOrNotService;
import edu.umich.si.inteco.minuku_2.stream.CheckFamiliarOrNotStream;
import edu.umich.si.inteco.minukucore.dao.DAOException;
import edu.umich.si.inteco.minukucore.exception.StreamAlreadyExistsException;
import edu.umich.si.inteco.minukucore.exception.StreamNotFoundException;
import edu.umich.si.inteco.minukucore.stream.Stream;

import static android.content.Context.NOTIFICATION_SERVICE;

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

    private int notifyID=1;

    private final String link = "https://qtrial2017q2az1.az1.qualtrics.com/jfe/form/SV_0VA9kDhoEeWHuYd";
    private String NotificationText = "Please click to fill the questionnaire";

    private String transportation;
    private int staticornot;

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
        Intent intent = new Intent(mContext, CheckFamiliarOrNotService.class);
        mContext.startService(intent);
//        PendingIntent.getService(mContext, 0, intent, 0); //startService

    }

    public void startCheckFamiliarOrNotMainThread() {
        //Log.e(TAG,"startCheckFamiliarOrNotMainThread");
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
/*
            String _mcog = "http://mcog.asc.ohio-state.edu/apps/pip?lon=120.990079&lat=24.784819&userid=6&format=json";
            try{
                JSONObject json = readJsonFromUrl(_mcog);
                Log.d(TAG,json.toString());

                Log.e(TAG,"inhome : "+json.get("inhome")+" inhrsd : "+json.get("inhrsd")+" outhr : "+json.get("outhr"));

            }catch (JSONException e){
                Log.e(TAG,"JSONException");
                e.printStackTrace();
            }catch (IOException e){
                Log.e(TAG,"IOException");
                e.printStackTrace();
            }
*/

            System.out.print("CheckFamiliarOrNotRunnable");
            Log.d(TAG,"CheckFamiliarOrNotRunnable");
            //initialize
            home=0;
            neighbor=0;
            outside=0;
/*
            //get latitude and longitude from LocationStreamGenerator
            try {
                latitude = LocationStreamGenerator.toCheckFamiliarOrNotLocationDataRecord.getLatitude();
                longitude = LocationStreamGenerator.toCheckFamiliarOrNotLocationDataRecord.getLongitude();
                Log.e(TAG + " testing server", "latitude : " + latitude + " longitude : " + longitude);
            }catch (Exception e){
                Log.e(TAG,"No Location, yet.");
            }
            int userid = 6;

            //TODO check the server
            String mcog = "http://mcog.asc.ohio-state.edu/apps/pip?" +
                    "lon="+String.valueOf(longitude) +
                    "&lat="+String.valueOf(latitude) +
                    "&userid=6"+ //+String.valueOf(userid) +
                    "&tsphone=999999&pdop=10.2" +
                    "&format=json";

            Log.e(TAG,"mcog - " + mcog);

            try{
                JSONObject json = readJsonFromUrl(mcog);
                Log.d(TAG,json.toString());

                Log.e(TAG,"inhome : "+json.get("inhome")+" inhrsd : "+json.get("inhrsd")+" outhr : "+json.get("outhr"));

                home = Integer.valueOf((String) json.get("inhome"));
                neighbor = Integer.valueOf((String) json.get("inhrsd"));
                outside = Integer.valueOf((String) json.get("outhr"));

                Log.e(TAG,"inhome : "+home+" inhrsd : "+neighbor+" outhr : "+outside);

            }catch (JSONException e){
                Log.e(TAG,"JSONException");
                e.printStackTrace();
            }catch (IOException e){
                Log.e(TAG,"IOException");
                e.printStackTrace();
            }
*/

            //randomize where the user places is. 60% 30% 10%
            Random rand = new Random();
            int num = rand.nextInt(100) + 1;

            Log.e(TAG,"num : " + String.valueOf(num));
            if(num>=1&&num<=60)
                home=1;
            else if(num>=61&&num<=90)
                neighbor=1;
            else if(num>=91&&num<=100)
                outside=1;
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
            CheckFamiliarOrNotDataRecord checkFamiliarOrNotDataRecord =
                   new CheckFamiliarOrNotDataRecord(home,neighbor,outside); //staticornot,

            //queryForCheckingDuration(staticornot,home,neighbor,outside);

            if(false){ //TODO check data on sqllite.

            }else{

                showTransportationAndIsHome();

                triggerQualtrics();

                mDAO.insert(checkFamiliarOrNotDataRecord);

            }


        }
    };

    private void triggerQualtrics(){

        Log.e(TAG,"triggerQualtrics");

        int homeorfaraway=0;
        if(home==1)
            homeorfaraway = 1;
        else if(neighbor==1)
            homeorfaraway = 2;
        else if(outside==1)
            homeorfaraway = 3;

        transportation = TransportationModeStreamGenerator.toCheckFamiliarOrNotTransportationModeDataRecord.getConfirmedActivityType();

        determineByContextQualtrics(homeorfaraway,transportation);

    }

    public void determineByContextQualtrics(int homeorfaraway, String transportation){
        String notiText = "Context is not existed."; //by default
        if(homeorfaraway==1&&!transportation.equals("static")) {
            notiText = "Trigger for home, moving";

            Random rand = new Random();
            int num = rand.nextInt(100) + 1;
            //15% to trigger
            if(num>=1&&num<=15)
                notiQualtrics(notiText);

        }
        else if(homeorfaraway==1&&transportation.equals("static")) {
            notiText = "Trigger for home, not moving";

            Random rand = new Random();
            int num = rand.nextInt(10) + 1;
            //10% to trigger
            if(num==1)
                notiQualtrics(notiText);
        }
        else if(homeorfaraway==2&&!transportation.equals("static")) {
            notiText = "Trigger for near home, moving";

            Random rand = new Random();
            int num = rand.nextInt(10) + 1;
            //30% to trigger
            if(num>=1&&num<=3)
                notiQualtrics(notiText);
        }
        else if(homeorfaraway==2&&transportation.equals("static")) {
            notiText = "Trigger for near home, not moving";

            Random rand = new Random();
            int num = rand.nextInt(10) + 1;
            //30% to trigger
            if(num>=1&&num<=3)
                notiQualtrics(notiText);
        }
        else if(homeorfaraway==3&&!transportation.equals("static")) {
            notiText = "Trigger for faraway, moving";

            Random rand = new Random();
            int num = rand.nextInt(2) + 1;
            //50% to trigger
            if(num==1)
                notiQualtrics(notiText);
        }
        else if(homeorfaraway==3&&transportation.equals("static")) {
            notiText = "Trigger for faraway, not moving";

            Random rand = new Random();
            int num = rand.nextInt(2) + 1;
            //50% to trigger
            if(num==1)
                notiQualtrics(notiText);
        }
        Log.e(TAG,notiText);



        // need to roll
        // notiQualtrics(notiText);

    }

    private void notiQualtrics(String notiText){

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
        mNotificationManager.notify(2, note.build()); //String.valueOf(System.currentTimeMillis()),
        note.build().flags = Notification.FLAG_AUTO_CANCEL;

    }

    private void showTransportationAndIsHome(){

        Log.e(TAG,"showTransportationAndIsHome");

        String inhomeornot="not working yet";

        if(home==1)
            inhomeornot = "at home";
        else if(neighbor==1)
            inhomeornot = "at neighbor";
        else if(outside==1)
            inhomeornot = "at outside";

        NotificationText = "Transportation Mode: " + TransportationModeStreamGenerator.toCheckFamiliarOrNotTransportationModeDataRecord.getConfirmedActivityType()
                +"\r\n"+"Is_home: "+inhomeornot;

        Log.e(TAG,"getConfirmedActivityType : " + TransportationModeStreamGenerator.toCheckFamiliarOrNotTransportationModeDataRecord.getConfirmedActivityType());

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);//Context.

        final Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        bigTextStyle.setBigContentTitle("Minuku2");
        bigTextStyle.bigText(NotificationText);

        Notification.Builder note = new Notification.Builder(mContext)
                .setContentTitle(Constants.APP_NAME)
                .setContentText(NotificationText)
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
        mNotificationManager.notify(notifyID, note.build()); //String.valueOf(System.currentTimeMillis()),
        note.build().flags = Notification.FLAG_AUTO_CANCEL;

        //note.setContentText(NotificationText);
        //mNotificationManager.notify(String.valueOf(System.currentTimeMillis()), 1, note.build());
    }

    @Override
    public Stream<CheckFamiliarOrNotDataRecord> generateNewStream() {
        return mStream;
    }

    @Override
    public boolean updateStream() {
        CheckFamiliarOrNotDataRecord checkFamiliarOrNotDataRecord =
                new CheckFamiliarOrNotDataRecord(home,neighbor,outside); //staticornot,
        mStream.add(checkFamiliarOrNotDataRecord);
        Log.d(TAG, "TransportationMode to be sent to event bus" + checkFamiliarOrNotDataRecord);
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

    private void queryForCheckingDuration(int staticornot,int home,int neighbor,int outside){
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor checkDuration = db.rawQuery("SELECT * FROM "+ DBHelper.checkFamiliarOrNot_table+"WHERE "+
                DBHelper.staticornot+"="+staticornot+
                DBHelper.home_col+"="+home+
                DBHelper.neighbor_col+"="+neighbor+
                DBHelper.outside_col+"="+outside+
                DBHelper.id+"= (SELECT MAX("+DBHelper.id+")  FROM "+ DBHelper.checkFamiliarOrNot_table+")"
                , null);

        checkDuration.getString(checkDuration.getColumnIndex(DBHelper.TIME));

        Log.e(TAG, "recent time" + checkDuration.getString(checkDuration.getColumnIndex(DBHelper.TIME)));
    }

    @Override
    public long getUpdateFrequency() {
        return 15;
    }

    @Override
    public void sendStateChangeEvent() {

    }

    public void setDAO(CheckFamiliarOrNotDataRecord checkFamiliarOrNotDataRecord){
        mDAO.insert(checkFamiliarOrNotDataRecord);
    }

    @Override
    public void offer(CheckFamiliarOrNotDataRecord dataRecord) {

    }
}
