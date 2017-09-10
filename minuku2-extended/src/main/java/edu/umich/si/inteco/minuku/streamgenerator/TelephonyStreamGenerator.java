package edu.umich.si.inteco.minuku.streamgenerator;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import edu.umich.si.inteco.minuku.config.Constants;
import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku.model.DataRecord.TelephonyDataRecord;
import edu.umich.si.inteco.minuku.stream.TelephonyStream;
import edu.umich.si.inteco.minukucore.exception.StreamAlreadyExistsException;
import edu.umich.si.inteco.minukucore.exception.StreamNotFoundException;
import edu.umich.si.inteco.minukucore.stream.Stream;

import static edu.umich.si.inteco.minuku.manager.MinukuStreamManager.getInstance;

/**
 * Created by Lucy on 2017/9/6.
 */

public class TelephonyStreamGenerator extends AndroidStreamGenerator<TelephonyDataRecord> {

    private String TAG = "TelephonyStreamGenerator";
    private Stream mStream;
    private TelephonyManager telephonyManager;
    private SignalStrength signalStrength;

    public TelephonyStreamGenerator (Context applicationContext) {
        super(applicationContext);
        this.mStream = new TelephonyStream(Constants.DEFAULT_QUEUE_SIZE);
        this.register();
    }
    @Override
    public void register() {
        Log.d(TAG, "Registring with StreamManage");

        try {
            getInstance().register(mStream, TelephonyDataRecord.class, this);
        } catch (StreamNotFoundException streamNotFoundException) {
            Log.e(TAG, "One of the streams on which" +
                    "RingerDataRecord/RingerStream depends in not found.");
        } catch (StreamAlreadyExistsException streamAlreadyExsistsException) {
            Log.e(TAG, "Another stream which provides" +
                    " TelephonyDataRecord/TelephonyStream is already registered.");
        }
    }

    @Override
    public Stream<TelephonyDataRecord> generateNewStream() {
        return mStream;
    }

    @Override
    public boolean updateStream() {
        return true;
    }

    @Override
    public long getUpdateFrequency() {
        return 15;
    }

    @Override
    public void sendStateChangeEvent() {

    }

    @Override
    public void onStreamRegistration() {

        telephonyManager = (TelephonyManager) mApplicationContext.getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = telephonyManager.getNetworkOperatorName();
        int networktype = telephonyManager.getNetworkType();

        telephonyManager.listen(callStateListener,PhoneStateListener.LISTEN_CALL_STATE|PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        switch (networktype) {
            case 0: Log.d(TAG, "UNKNOWN");
            case 1: Log.d(TAG, "GPRS");
            case 2: Log.d(TAG, "EDGE");
            case 3: Log.d(TAG, "UMTS");
            case 4: Log.d(TAG, "CDMA");
            case 5: Log.d(TAG, "EVDO_0");
            case 6: Log.d(TAG, "EVDO_A");
            case 7: Log.d(TAG, "1xRTT");
            case 8: Log.d(TAG, "HSDPA");
            case 9: Log.d(TAG, "HSUPA");
            case 10: Log.d(TAG, "HSPA");
            case 11: Log.d(TAG, "IDEN");
            case 12: Log.d(TAG, "EVDO_B");
            case 13: Log.d(TAG, "LTE");
            case 14: Log.d(TAG, "EHRPD");
            case 15: Log.d(TAG, "HSPAP");
            case 16: Log.d(TAG, "GSM");
            case 17: Log.d(TAG, "TD_SCDMA");
            case 18: Log.d(TAG, "IWLAN");
        }

        Log.d(TAG, carrierName);
    }
    private final PhoneStateListener callStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {
            if(state==TelephonyManager.CALL_STATE_RINGING){
                Log.d(TAG, "ringing");
            }
            if(state==TelephonyManager.CALL_STATE_OFFHOOK){
                Log.d(TAG, "answering");
            }
            if(state==TelephonyManager.CALL_STATE_IDLE){
                Log.d(TAG, "idle");
            }
        }
        public void onSignalStrengthsChanged(SignalStrength sStrength) {
            String ssignal = sStrength.toString();
            String[] parts = ssignal.split(" ");

            int dbm;
            int asu;

            //If LTE 4G
            if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE){

                dbm = Integer.parseInt(parts[10]);
                asu = 140 + dbm;
                Log.d("LTE Signal strength (dbm)", String.valueOf(dbm));
                Log.d("LTE Signal strength (asu)", String.valueOf(asu));
            }
            // Else 3G
            else {
                if (signalStrength.isGsm()) {
                    // For GSM Signal Strength: dbm =  (2*ASU)-113.
                    if (signalStrength.getGsmSignalStrength() != 99) {
                        int intdbm = -113 + 2 * signalStrength.getGsmSignalStrength();
                        dbm = intdbm;
                        Log.d("GSM Signal strength", String.valueOf(dbm));
                    }
                    else {
                        dbm = signalStrength.getGsmSignalStrength();
                    }
                }
                else {
                    // cdma
                    dbm = signalStrength.getCdmaDbm();
                    Log.d("CDMA Signal strength", String.valueOf(dbm));
                }

            }
        }
    };

    @Override
    public void offer(TelephonyDataRecord dataRecord) {

    }
}
