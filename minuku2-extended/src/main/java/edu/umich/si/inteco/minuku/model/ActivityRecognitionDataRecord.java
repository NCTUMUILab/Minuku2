package edu.umich.si.inteco.minuku.model;

import com.google.android.gms.location.DetectedActivity;

import java.util.Date;

import edu.umich.si.inteco.minukucore.model.DataRecord;

/**
 * Created by Lawrence on 2017/5/22.
 */

public class ActivityRecognitionDataRecord implements DataRecord {

    public long creationTime;
    private static DetectedActivity MostProbableActivity;
    private static long Detectedtime;

    public ActivityRecognitionDataRecord(){

    }

    public ActivityRecognitionDataRecord(DetectedActivity MostProbableActivity,long Detectedtime){
        this.creationTime = new Date().getTime();
        this.MostProbableActivity = MostProbableActivity;
        this.Detectedtime = Detectedtime;

    }

    public DetectedActivity getMostProbableActivity(){return MostProbableActivity;}

    public long getDetectedtime(){return Detectedtime;}

    @Override
    public long getCreationTime() {
        return creationTime;
    }
}