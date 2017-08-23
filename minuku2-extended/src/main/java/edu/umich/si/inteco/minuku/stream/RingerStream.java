package edu.umich.si.inteco.minuku.stream;

import java.util.ArrayList;
import java.util.List;

import edu.umich.si.inteco.minuku.model.DataRecord.RingerDataRecord;
import edu.umich.si.inteco.minukucore.model.DataRecord;
import edu.umich.si.inteco.minukucore.stream.AbstractStreamFromDevice;

/**
 * Created by Lucy on 2017/8/14.
 */

public class RingerStream extends AbstractStreamFromDevice<RingerDataRecord> {
    public RingerStream(int maxSize) {
        super(maxSize);
    }

    @Override
    public List<Class<? extends DataRecord>> dependsOnDataRecordType() {
        return new ArrayList<>();
    }
}
