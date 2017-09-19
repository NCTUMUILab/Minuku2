package edu.umich.si.inteco.minuku_2.controller.Ohio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import edu.umich.si.inteco.minuku_2.R;

/**
 * Created by Lawrence on 2017/7/5.
 */

public class recordinglistohio extends Activity {

    final private static String TAG = "recordinglistohio";

    private Context mContext;
    private ListView listview;
    private String[] list = {"2017/7/5 10:00","2017/7/6 11:00"};
    private ArrayAdapter<String> listAdapter;

    public recordinglistohio(){}

    public recordinglistohio(Context mContext){
        this.mContext = mContext;
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recordinglist_ohio);



    }

    @Override
    public void onResume() {
        super.onResume();

        initrecordinglistohio();
    }

    private void initrecordinglistohio(){

        listview = (ListView)findViewById(R.id.recording_list);

        listAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);

        listview.setAdapter(listAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                startActivity(new Intent(recordinglistohio.this, annotateohio.class));

            }
        });

    }

    /*private class ListRecordAsyncTask extends AsyncTask<String, Void, ArrayList<Session>> {
        private final ProgressDialog dialog = new ProgressDialog(recordinglistohio.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected ArrayList<Session> doInBackground(String... params) {

            String reviewMode = params[0];
            Log.d(TAG, "listRecordAsyncTask going to list recording with mode" + reviewMode);

            ArrayList<Session> sessions =null;





            return sessions;

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ArrayList<Session> result) {
            super.onPostExecute(result);

            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }

    }*/


}
