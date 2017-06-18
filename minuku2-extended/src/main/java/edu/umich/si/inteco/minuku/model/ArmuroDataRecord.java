package edu.umich.si.inteco.minuku.model;

import java.util.Date;

import edu.umich.si.inteco.minukucore.model.DataRecord;

/**
 * Created by Lawrence on 2017/6/18.
 */

public class ArmuroDataRecord implements DataRecord{

    public long creationTime;

    public ArmuroDataRecord(){
        this.creationTime = new Date().getTime();

    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }
}
