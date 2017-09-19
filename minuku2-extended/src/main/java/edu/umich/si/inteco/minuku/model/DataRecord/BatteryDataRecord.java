package edu.umich.si.inteco.minuku.model.DataRecord;


import java.util.Date;
import edu.umich.si.inteco.minukucore.model.DataRecord;

/**
 * Created by Lucy on 2017/8/11.
 */

public class BatteryDataRecord implements DataRecord {
    public String base64Data;
    public long creationTime;

    public BatteryDataRecord(String base64Data) {
        this.base64Data = base64Data;
        this.creationTime = new Date().getTime();
    }

    @Override
    public long getCreationTime() {

        return creationTime;
    }
}
