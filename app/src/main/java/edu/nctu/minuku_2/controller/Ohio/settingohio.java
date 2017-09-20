package edu.nctu.minuku_2.controller.Ohio;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import edu.nctu.minuku.logger.Log;
import edu.nctu.minuku_2.R;

/**
 * Created by Lawrence on 2017/7/5.
 */

public class settingohio extends AppCompatActivity {

    final String TAG = "settingohio";

    private Context mContext;
    private Button starttime, endtime, confirm;

    public settingohio(){}

    public settingohio(Context mContext){
        this.mContext = mContext;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingohio);

        initsettingohio();

    }

    public void initsettingohio(){

        starttime = (Button)findViewById(R.id.starttime);
        starttime.setOnClickListener(starttimeing);

        endtime = (Button)findViewById(R.id.endtime);
        endtime.setOnClickListener(endtimeing);

        confirm = (Button)findViewById(R.id.confirm);
        confirm.setOnClickListener(confirming);
    }

    private Button.OnClickListener confirming = new Button.OnClickListener() {
        public void onClick(View v) {
            Log.e(TAG, "confirm clicked");
            if(starttime.getText().equals("Please select your start time"))
                Toast.makeText(settingohio.this,"Please select your start time!!",Toast.LENGTH_SHORT);
            else if(endtime.getText().equals("Please select your end time"))
                Toast.makeText(settingohio.this,"Please select your end time!!",Toast.LENGTH_SHORT);
            else {
                Intent intent = getIntent();
                Bundle bundle = new Bundle();
                bundle.putString("SleepingStartTime",starttime.getText().toString());
                bundle.putString("SleepingEndTime",endtime.getText().toString());
                intent.putExtras(bundle);
                setResult(1, intent);
                settingohio.this.finish();
            }
        }
    };

    private Button.OnClickListener starttimeing = new Button.OnClickListener() {
        public void onClick(View v) {
            Log.e(TAG,"starttime clicked");

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            new TimePickerDialog(settingohio.this, new TimePickerDialog.OnTimeSetListener(){
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    String hour=String.valueOf(hourOfDay);
                    String min =String.valueOf(minute);
                    if(hourOfDay<10)
                        hour = "0" + String.valueOf(hourOfDay);

                    if(minute<10)
                        min = "0" + String.valueOf(minute);

                    starttime.setText( hour + ":" + min);

                    /*SharedPreferences.Editor editor = getSharedPreferences("edu.nctu.minuku_2", MODE_PRIVATE).edit();
                    editor.putString("SleepingStartTime",starttime.getText().toString());
                    editor.commit();*/

                }
            }, hour, minute, false).show();

        }
    };

    private Button.OnClickListener endtimeing = new Button.OnClickListener() {
        public void onClick(View v) {
            Log.e(TAG,"endtime clicked");

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            new TimePickerDialog(settingohio.this, new TimePickerDialog.OnTimeSetListener(){
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    String hour=String.valueOf(hourOfDay);
                    String min =String.valueOf(minute);
                    if(hourOfDay<10)
                        hour = "0" + String.valueOf(hourOfDay);

                    if(minute<10)
                        min = "0" + String.valueOf(minute);

                    endtime.setText( hour + ":" + min);

                    /*SharedPreferences.Editor editor = getSharedPreferences("edu.nctu.minuku_2", MODE_PRIVATE).edit();
                    editor.putString("SleepingEndTime",endtime.getText().toString());
                    editor.commit();*/

                }
            }, hour, minute, false).show();
        }
    };

}
