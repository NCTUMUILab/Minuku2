package edu.umich.si.inteco.minuku_2.controller.Ohio;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import edu.umich.si.inteco.minuku_2.R;

/**
 * Created by Lawrence on 2017/7/5.
 */

public class recordinglistohio extends AppCompatActivity{
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


}
