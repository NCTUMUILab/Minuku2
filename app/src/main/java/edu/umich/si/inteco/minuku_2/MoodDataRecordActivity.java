/*
 * Copyright (c) 2016.
 *
 * DReflect and Minuku Libraries by Shriti Raj (shritir@umich.edu) and Neeraj Kumar(neerajk@uci.edu) is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Based on a work at https://github.com/Shriti-UCI/Minuku-2.
 *
 *
 * You are free to (only if you meet the terms mentioned below) :
 *
 * Share — copy and redistribute the material in any medium or format
 * Adapt — remix, transform, and build upon the material
 *
 * The licensor cannot revoke these freedoms as long as you follow the license terms.
 *
 * Under the following terms:
 *
 * Attribution — You must give appropriate credit, provide a link to the license, and indicate if changes were made. You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.
 * NonCommercial — You may not use the material for commercial purposes.
 * ShareAlike — If you remix, transform, or build upon the material, you must distribute your contributions under the same license as the original.
 * No additional restrictions — You may not apply legal terms or technological measures that legally restrict others from doing anything the license permits.
 */

package edu.umich.si.inteco.minuku_2;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.text.DecimalFormat;
import java.util.Date;

import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku.manager.MinukuStreamManager;
import edu.umich.si.inteco.minuku.model.DataRecordUtil;
import edu.umich.si.inteco.minuku.model.MoodDataRecord;
import edu.umich.si.inteco.minuku.model.UserSubmissionStats;
import edu.umich.si.inteco.minuku_2.manager.InstanceManager;
import edu.umich.si.inteco.minuku_2.view.customview.MoodEntryView;
import edu.umich.si.inteco.minukucore.exception.StreamNotFoundException;

/**
 * Created by shriti on 7/21/16.
 */
public class MoodDataRecordActivity extends BaseActivity {

    private float DEVICE_DENSITY = 0;
    private int DEVICE_WIDTH = 0;

    private float tapX = 0, tapY = 0;
    private float tap_X = 0, tap_Y = 0;
    float firstMoodX = 0, firstMoodY = 0, secondMoodX = 0, secondMoodY = 0;
    float scale;

    MoodDataRecord moodFirst = new MoodDataRecord();
    MoodDataRecord moodSecond = new MoodDataRecord();

    MoodEntryView moodEntryView;
    private Button btnTrends;

    private String TAG ="MoodDataRecordActivity";
    private Context contextForDialog = null;
    private Dialog helpDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        scale = this.getResources().getDisplayMetrics().density;
        setContentView(R.layout.add_mood_activity);

        contextForDialog = this;
        // Add click listeners for buttons
        Button acceptButton = (Button) findViewById(R.id.acceptButton);
        Button rejectButton = (Button) findViewById(R.id.rejectButton);
        ImageView helpButton = (ImageView) findViewById(R.id.help_button);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptResults();
            }
        });
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectResults();
            }
        });
        /*helpButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    findViewById(R.id.mood_legend_overlay).setVisibility(View.VISIBLE);
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    findViewById(R.id.mood_legend_overlay).setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });*/
        helpButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        onHelpClicked();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (helpDialog != null) {
                            helpDialog.hide();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
//        helpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onHelpClicked();
//            }
//        });
        getDeviceWidth();
        initUi();
    }

    private void getDeviceWidth() {
        DEVICE_DENSITY = getResources().getDisplayMetrics().density;
        DEVICE_WIDTH = getResources().getDisplayMetrics().widthPixels;
        Log.d(TAG, "**Device Density=" + DEVICE_DENSITY + " " + "DEVICE_WIDTH="
                + DEVICE_WIDTH);
    }

    private void initUi() {
        android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
                DEVICE_WIDTH, DEVICE_WIDTH);
        moodEntryView = (MoodEntryView) findViewById(R.id.graphCustomView);
        moodEntryView.setLayoutParams(lp);
        moodEntryView.setOnTouchListener(moodEntryViewOncliClickListener);
    }

    View.OnTouchListener moodEntryViewOncliClickListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                // is first mood selected
                if (event.getX() - 25 * scale < moodFirst.x
                        && event.getX() + 25 * scale > moodFirst.x
                        && event.getY() - 25 * scale < moodFirst.y
                        && event.getY() + 25 * scale > moodFirst.y)
                {
                    Log.d("First Mood", "Selected");
                    moodFirst.isSelected = true;
                    moodSecond.isSelected = false;
                }

                // is second mood selected
                else if (event.getX() - (25 * scale) < moodSecond.x
                        && event.getX() + (25 * scale) > moodSecond.x
                        && event.getY() - (25 * scale) < moodSecond.y
                        && event.getY() + (25 * scale) > moodSecond.y)
                {
                    Log.d("Second Mood", "Selected");
                    moodFirst.isSelected = false;
                    moodSecond.isSelected = true;
                }
                // else no mood is selected
                else
                {
                    Log.d("No Mood", "Selected");
                    moodFirst.isSelected = false;
                    moodSecond.isSelected = false;
                }

                /** Choice first or second mood creating */
                if (!moodFirst.isCreated && moodSecond.isSelected == false)
                {
                    moodFirst.x = event.getX();
                    moodFirst.y = event.getY();
                    moodFirst.isCreated = true;
                    moodFirst.isSelected = true;
                    setFirstMood(moodFirst);
                }

                //showOrHideArrowButton();
            }

            if (event.getAction() == MotionEvent.ACTION_UP)
            {

                float X, Y;
                X = event.getX();
                Y = event.getY();

                float viewWidth = moodEntryView.getWidth();
                float rectSize = viewWidth / 23;
                // float rectSize = viewWidth/23;
                Log.d("X* *,Y***", "==" + X + " , " + Y);

                if (X < ((viewWidth / 2) - 10 * scale) && (Y < (viewWidth / 2)))
                {
                    tapX = (float) ((Math.floor((viewWidth / 2) - X) / rectSize) * -1);// X
                    // should
                    // (-ve)
                    // in
                    // first
                    // quadrant
                    tapY = (float) (Math.floor((viewWidth / 2) - Y) / rectSize);
                    Log.d("Q", "1");
                }
                if ((X > (viewWidth / 2)) && (Y < (viewWidth / 2)))
                {
                    X = X - (viewWidth / 2);

                    /*
                     * tapX =(float)(Math.floor((X/rectSize))); tapY =
                     * (float)(Math.floor(((viewWidth/2)-Y)/rectSize)) ;
                     */
                    tapX = (X / rectSize);
                    tapY = ((viewWidth / 2) - Y) / rectSize;
                    Log.d("Q", "2");
                }
                if ((X > (viewWidth / 2)) && (Y > (viewWidth / 2)))
                {
                    X = X - (viewWidth / 2);
                    Y = Y - (viewWidth / 2);
                    /*
                     * tapX =(float)(Math.floor((X/rectSize))); tapY
                     * =(float)((Math.floor((Y/rectSize)))*-1);//Y should (-ve)
                     * in third quadrant
                     */
                    tapX = (X / rectSize);
                    tapY = (Y / rectSize) * (-1);// Y should (-ve) in third
                    // quadrant

                    Log.d("Q", "3");
                }
                if ((X < (viewWidth / 2)) && (Y > (viewWidth / 2)))
                {
                    /*
                     * tapX =(float)(Math.floor(((X-(viewWidth/2))/rectSize)));
                     * tapY =(float)(Math.floor(((viewWidth/2)-Y)/rectSize));
                     */
                    tapX = (X - (viewWidth / 2)) / rectSize;
                    tapY = ((viewWidth / 2) - Y) / rectSize;
                    Log.d("Q", "4");
                }

                if (tapX > 10.0)
                    tapX = (float) 10.0;
                else if (tapX < -10.0)
                    tapX = (float) -10.0;

                if (tapY > 10.0)
                    tapY = (float) 10.0;
                else if (tapY < -10.0)
                    tapY = (float) -10.0;

                Log.d("TAPX,TAPY", "" + tapX + " , " + tapY);

                DecimalFormat df = new DecimalFormat("#.#");
                String tapx = df.format(tapX);
                String tapy = df.format(tapY);
                tap_X = Float.parseFloat(tapx);
                tap_Y = Float.parseFloat(tapy);
                if (moodFirst.isSelected)
                {
                    moodFirst.x = event.getX();
                    moodFirst.y = event.getY();
                    moodFirst.moodLevel = tap_X;
                    moodFirst.energyLevel = tap_Y;

                }
                if (moodSecond.isSelected)
                {
                    moodSecond.x = event.getX();
                    moodSecond.y = event.getY();
                    moodSecond.moodLevel = tap_X;
                    moodSecond.energyLevel = tap_Y;
                }
                /** If mood drags */
                if (event.getX() < 10 * scale || event.getX() > moodEntryView.getWidth() - 10 * scale
                        || event.getY() < 10 * scale || event.getY() > moodEntryView.getWidth() - 10 * scale)
                {
                    if (moodFirst.isSelected)
                    {
                        moodFirst.x = -50 * scale;
                        moodFirst.y = -50 * scale;
                        setFirstMood(moodFirst);
                    }


                    moodEntryView.invalidate();
                    Log.d("Action", "Out of Bond");
                }
                moodFirst.isSelected = false;
                moodSecond.isSelected = false;
                //showOrHideArrowButton();
            }

            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                Log.d("Action", "Move");
                if (moodFirst.isSelected)
                {
                    Log.d("first Mood ", "Moving");
                    moodFirst.x = event.getX() - 10 * scale;
                    moodFirst.y = event.getY() - 10 * scale;
                    setFirstMood(moodFirst);
                }
                else
                {
                    Log.d("NoMood", "X=" + event.getX() + " Y=" + event.getY());
                }

                /** Delete the mood entry */
                if (event.getX() < 20 * scale || event.getX() > moodEntryView.getWidth() - 20 * scale
                        || event.getY() < 20 * scale || event.getY() > moodEntryView.getWidth() - 20 * scale)
                {
                    if (moodFirst.isSelected)
                    {
                        moodFirst.x = -20 * scale;
                        moodFirst.y = -20 * scale;
                        moodFirst.isCreated = false;
                        setFirstMood(moodFirst);
                    }

                    moodEntryView.invalidate();
                    Log.d("Action", "Out of Bond");
                }
            }

            Log.d("TAPX formated ,TAPY", "" + tap_X + " , " + tap_Y);
            return true;
        }
    };

    /**
     * Called each time the user touches anywhere on the mood map.
     * @param mood
     */
    void setFirstMood(MoodDataRecord mood) {
        moodEntryView.setFirstMood(mood.x, mood.y);
    }


    /**
     * This is called when the user pressed "Tick" button on the screen.
     */
    public void acceptResults() {

        // Confirm that a mood was indeed selected.
        if(!moodFirst.isCreated) {
            showToast("Please select a mood on the map above first.");
            return;
        }
        moodFirst.creationTime = new Date().getTime();

        //Create a new mood data record
        MoodDataRecord moodDataRecordToSave = moodFirst;
        moodDataRecordToSave.location = DataRecordUtil.attemptToGetSemanticOrNormalLocation();
        try {
            Log.d(TAG, "Saving mood to the stream and the database");
            MinukuStreamManager.getInstance()
                    .getStreamGeneratorFor(MoodDataRecord.class)
                    .offer(moodDataRecordToSave);
            UserSubmissionStats userSubmissionStats = InstanceManager
                    .getInstance(getApplicationContext())
                    .getUserSubmissionStats();

            //update reports submitted by user
            userSubmissionStats.incrementMoodCount();
            InstanceManager
                    .getInstance(getApplicationContext())
                    .setUserSubmissionStats(userSubmissionStats);
            showToast("Your mood has been recorded");
        } catch (StreamNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "The mood stream does not exist on this device");
            showToast("We had issues recording this data. An error report was sent to us.");
        }

        finish();
    }


    /**
     * This is called when the user presses the "X" button the screen.
     */
    public void rejectResults() {
        showToast("Going back to home screen");
        finish();
    }

    public void onHelpClicked() {
        helpDialog = new Dialog(contextForDialog);
        helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        helpDialog.setContentView(R.layout.mood_legend_overlay);
        helpDialog.setCanceledOnTouchOutside(false);
        helpDialog.setCancelable(true);

        ImageView overlay = (ImageView) helpDialog.findViewById(R.id.mood_legend_overlay);
       // overlay.setBackgroundColor(200 * 0x1000000);
//        ImageView closeButton = (ImageView) helpDialog.findViewById(R.id.close_button);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                helpDialog.dismiss();
//            }
//        });
        helpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        DisplayMetrics displayMetrics = contextForDialog.getResources().getDisplayMetrics();
        int dialogWidth = (int)(displayMetrics.widthPixels * 0.925);
        int dialogHeight = (int)(displayMetrics.heightPixels * 0.925);//90
        helpDialog.getWindow().setLayout(dialogWidth, dialogHeight);
        helpDialog.getWindow().setGravity(Gravity.TOP);

        helpDialog.show();
    }

    public void onCancelClicked(View view) {
        showToast("cancel clicked");
    }
}
