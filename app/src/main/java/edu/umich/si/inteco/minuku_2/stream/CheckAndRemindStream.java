package edu.umich.si.inteco.minuku_2.stream;

import java.util.ArrayList;
import java.util.List;

import edu.umich.si.inteco.minuku_2.model.CheckAndRemindDataRecord;
import edu.umich.si.inteco.minukucore.model.DataRecord;
import edu.umich.si.inteco.minukucore.stream.AbstractStreamFromDevice;

/**
 * Created by Lawrence on 2017/9/19.
 */

public class CheckAndRemindStream extends AbstractStreamFromDevice<CheckAndRemindDataRecord> {
    public CheckAndRemindStream(int maxSize) {
        super(maxSize);
    }

    @Override
    public List<Class<? extends DataRecord>> dependsOnDataRecordType() {
        return new ArrayList<>();
    }
}
