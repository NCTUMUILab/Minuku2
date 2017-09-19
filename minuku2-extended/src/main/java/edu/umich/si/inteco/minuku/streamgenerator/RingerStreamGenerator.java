package edu.umich.si.inteco.minuku.streamgenerator;

import android.content.Context;
import android.media.AudioManager;

import edu.umich.si.inteco.minuku.config.Constants;
import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku.model.DataRecord.RingerDataRecord;
import edu.umich.si.inteco.minuku.stream.RingerStream;
import edu.umich.si.inteco.minukucore.exception.StreamAlreadyExistsException;
import edu.umich.si.inteco.minukucore.exception.StreamNotFoundException;
import edu.umich.si.inteco.minukucore.stream.Stream;

import static edu.umich.si.inteco.minuku.manager.MinukuStreamManager.getInstance;

/**
 * Created by Lucy on 2017/8/14.
 */

public class RingerStreamGenerator extends AndroidStreamGenerator<RingerDataRecord> {

    private String TAG = "RingerStreamGenerator";
    private Stream mStream;

    public RingerStreamGenerator (Context applicationContext) {
        super(applicationContext);
        this.mStream = new RingerStream(Constants.DEFAULT_QUEUE_SIZE);
        this.register();
    }
    @Override
    public void register() {
        Log.d(TAG, "Registring with StreamManage");

        try {
            getInstance().register(mStream, RingerDataRecord.class, this);
        } catch (StreamNotFoundException streamNotFoundException) {
            Log.e(TAG, "One of the streams on which" +
                    "RingerDataRecord/RingerStream depends in not found.");
        } catch (StreamAlreadyExistsException streamAlreadyExsistsException) {
            Log.e(TAG, "Another stream which provides" +
                    " RingerDataRecord/RingerStream is already registered.");
        }
    }

    @Override
    public Stream<RingerDataRecord> generateNewStream() {
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
        new Threading().start();
        Log.d(TAG, "main thread finish");
    }

    @Override
    public void offer(RingerDataRecord aringerdataRecord) {
        mStream.add(aringerdataRecord);
    }

    public class Threading extends Thread{
        public void run() {
            Log.d(TAG, "thread is running");
            while (true) {
                try {
                    AudioManager audioManager = (AudioManager) mApplicationContext.getSystemService(Context.AUDIO_SERVICE);
                    int RingerMode = audioManager.getRingerMode();
                    int AudioMode = audioManager.getMode();
                    int StreamVolumeRing = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                    int StreamVolumeMusic = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    int StreamVolumeNotification = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
                    int StreamVolumeVoicecall = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                    int StreamVolumeSystem = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);

                    switch (RingerMode) {
                        case AudioManager.RINGER_MODE_SILENT:
                            Log.d("RingerMode", "Silent mode");
                            break;
                        case AudioManager.RINGER_MODE_VIBRATE:
                            Log.d("RingerMode", "Vibrate mode");
                            break;
                        case AudioManager.RINGER_MODE_NORMAL:
                            Log.d("RingerMode", "Normal mode");
                            break;
                    }
                    switch (AudioMode) {
                        case AudioManager.MODE_NORMAL:
                            Log.d("AudioMode", "Normal mode");
                            break;
                        case AudioManager.MODE_RINGTONE:
                            Log.d("AudioMode", "Ring Tone");
                            break;
                        case AudioManager.MODE_IN_CALL:
                            Log.d("AudioMode", "In call");
                            break;
                        case AudioManager.MODE_IN_COMMUNICATION:
                            Log.d("AudioMode", "In Communication");
                            break;
                    }

                    Log.d("StreamVolumeRing", String.valueOf(StreamVolumeRing));
                    Log.d("StreamVolumeMusic", String.valueOf(StreamVolumeMusic));
                    Log.d("StreamVolumeNotification", String.valueOf(StreamVolumeNotification));
                    Log.d("StreamVolumeVoicecall", String.valueOf(StreamVolumeVoicecall));
                    Log.d("StreamVolumeSystem", String.valueOf(StreamVolumeSystem));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // 10* 1000 = run thread every 10 sec
                        Thread.sleep(10 * 1000);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }
}
