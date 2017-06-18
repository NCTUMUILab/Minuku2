package edu.umich.si.inteco.minuku.streamgenerator;

import edu.umich.si.inteco.minuku.model.ArmuroDataRecord;
import edu.umich.si.inteco.minukucore.stream.Stream;

/**
 * Created by Lawrence on 2017/6/18.
 */

public class ArmuroStreamGenerator extends AndroidStreamGenerator<ArmuroDataRecord>{

    @Override
    public void register() {

    }

    @Override
    public Stream<ArmuroDataRecord> generateNewStream() {
        return null;
    }

    @Override
    public boolean updateStream() {
        return false;
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

    }

    @Override
    public void offer(ArmuroDataRecord dataRecord) {

    }
}
