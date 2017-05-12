package edu.umich.si.inteco.minuku_2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Future;

import edu.umich.si.inteco.minuku.dao.MoodDataRecordDAO;
import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku.manager.MinukuDAOManager;
import edu.umich.si.inteco.minuku.manager.MinukuStreamManager;
import edu.umich.si.inteco.minuku.model.MoodDataRecord;
import edu.umich.si.inteco.minuku_2.cards.TimelineCardsAdapter;
import edu.umich.si.inteco.minuku_2.dao.DiabetesLogDAO;
import edu.umich.si.inteco.minuku_2.model.DiabetesLogDataRecord;
import edu.umich.si.inteco.minukucore.dao.DAO;
import edu.umich.si.inteco.minukucore.dao.DAOException;
import edu.umich.si.inteco.minukucore.exception.StreamNotFoundException;
import edu.umich.si.inteco.minukucore.manager.DAOManager;
import edu.umich.si.inteco.minukucore.model.DataRecord;
import edu.umich.si.inteco.minukucore.model.LocationBasedDataRecord;
import edu.umich.si.inteco.minukucore.stream.Stream;

/**
 * Created by neerajkumar on 10/14/16.
 */
public class EODTimelineActivity extends BaseActivity {
    private static final String TAG = "EODTimelineActivity";

    private RecyclerView mRecyclerView;
    private TimelineCardsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View mPreloader;
    private View mFailureContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eod_timeline);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(EODTimelineActivity.this);
        mPreloader = findViewById(R.id.preloader);
        mPreloader.setVisibility(View.VISIBLE);
        mFailureContent = findViewById(R.id.failureContent);

        new DataFetchTask().execute();
    }

    private void showFailureContent(Exception e) {
        Log.e(TAG, e.getMessage());
        mPreloader.setVisibility(View.GONE);
        mFailureContent.setVisibility(View.VISIBLE);
    }

    private class DataFetchTask extends AsyncTask<Void, Void, List<LocationBasedDataRecord>> {

        @Override
        protected List<LocationBasedDataRecord> doInBackground(Void... voids) {
            List<LocationBasedDataRecord> dataSet = new ArrayList<>();

            try {
                MoodDataRecordDAO moodDataRecordDAO = MinukuDAOManager.getInstance().getDaoFor(
                        MoodDataRecord.class);
                Future<List<MoodDataRecord>> moodDataRecordFuture = moodDataRecordDAO.getAll();

                DiabetesLogDAO diabetesLogDAO = MinukuDAOManager.getInstance().getDaoFor(
                        DiabetesLogDataRecord.class);
                Future<List<DiabetesLogDataRecord>> diabetesLogDataRecordFuture =
                        diabetesLogDAO.getAll();

                int waitCount = 0;
                while (!moodDataRecordFuture.isDone() &&
                        !diabetesLogDataRecordFuture.isDone() &&
                        waitCount < 30) {
                    Log.d(TAG, "Waiting to get info..." + waitCount);
                    Thread.sleep(500);
                    waitCount++;
                }
                boolean isDataLoaded = moodDataRecordFuture.isDone()
                        && diabetesLogDataRecordFuture.isDone();

                if(isDataLoaded) {
                    dataSet.addAll(moodDataRecordFuture.get());
                    dataSet.addAll(diabetesLogDataRecordFuture.get());
                    Collections.sort(dataSet, new Comparator<DataRecord>(){
                        public int compare(DataRecord o1, DataRecord o2){
                            return o1.getCreationTime() < o2.getCreationTime() ? -1 : 1;
                        }
                    });
                } else {
                    showFailureContent(new Exception("Could not load data in time."));
                }
            } catch (Exception e) {
                showFailureContent(e);
            }
            return dataSet;
        }

        @Override
        protected void onPostExecute(List<LocationBasedDataRecord> dataSet) {
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new TimelineCardsAdapter(dataSet, getApplicationContext());
            mRecyclerView.setAdapter(mAdapter);
            mPreloader.setVisibility(View.GONE);
        }
    }

}
