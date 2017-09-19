package edu.umich.si.inteco.minuku_2.model;

import java.util.Date;

import edu.umich.si.inteco.minukucore.model.DataRecord;

/**
 * Created by Lawrence on 2017/9/19.
 */

public class CheckAndRemindDataRecord implements DataRecord {

    public long creationTime;

    public CheckAndRemindDataRecord(){
        this.creationTime = new Date().getTime();

    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }
}
