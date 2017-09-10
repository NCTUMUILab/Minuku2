package edu.umich.si.inteco.minuku.stream;

import java.util.ArrayList;
import java.util.List;


import edu.umich.si.inteco.minuku.model.DataRecord.TelephonyDataRecord;
import edu.umich.si.inteco.minukucore.model.DataRecord;
import edu.umich.si.inteco.minukucore.stream.AbstractStreamFromDevice;

/**
 * Created by Lucy on 2017/9/6.
 */

public class TelephonyStream extends AbstractStreamFromDevice<TelephonyDataRecord> {
    public TelephonyStream(int maxSize) {
        super(maxSize);
    }

    @Override
    public List<Class<? extends DataRecord>> dependsOnDataRecordType() {
        return new ArrayList<>();
    }
}
