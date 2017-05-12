package edu.umich.si.inteco.minuku_2;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.graphics.Bitmap;


import com.firebase.client.Firebase;
import com.firebase.client.core.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import edu.umich.si.inteco.minuku.config.Constants;
import edu.umich.si.inteco.minuku.config.UserPreferences;
import edu.umich.si.inteco.minuku.dao.MoodDataRecordDAO;
import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku.manager.MinukuDAOManager;
import edu.umich.si.inteco.minuku.model.MoodDataRecord;
import edu.umich.si.inteco.minuku_2.cards.ButtonsViewHolder;
import edu.umich.si.inteco.minuku_2.cards.DiaryLogViewHolder;
import edu.umich.si.inteco.minuku_2.cards.MoodViewHolder;
import edu.umich.si.inteco.minuku_2.cards.TimelineCardsAdapter;
import edu.umich.si.inteco.minuku_2.dao.DiabetesLogDAO;
import edu.umich.si.inteco.minuku_2.dao.EODQuestionAnswerDAO;
import edu.umich.si.inteco.minuku_2.dao.TimelinePatchDataRecordDAO;
import edu.umich.si.inteco.minuku_2.event.TimelineNextPageEvent;
import edu.umich.si.inteco.minuku_2.event.TimelinePrevPageEvent;
import edu.umich.si.inteco.minuku_2.model.DiabetesLogDataRecord;
import edu.umich.si.inteco.minuku_2.model.EODQuestionDataRecord;
import edu.umich.si.inteco.minuku_2.model.Screenshot;
import edu.umich.si.inteco.minuku_2.model.TimelinePatchDataRecord;
import edu.umich.si.inteco.minuku_2.preferences.ApplicationConstants;
import edu.umich.si.inteco.minukucore.dao.DAOException;
import edu.umich.si.inteco.minukucore.model.DataRecord;
import edu.umich.si.inteco.minukucore.model.LocationBasedDataRecord;

/**
 * Created by shriti on 10/12/16.
 */

public class EODQuestionsActivity extends BaseActivity {

    ViewPager viewPager;
    MyPagerAdapter myPagerAdapter;
    EODQuestionDataRecord eodQuestionDataRecord = new EODQuestionDataRecord();
    String TAG = "EODQuestionsActivity";
    EODQuestionAnswerDAO eodQuestionAnswerDAO = new EODQuestionAnswerDAO();

    // Recycler view stuff.
    private RecyclerView mRecyclerView;
    private TimelineCardsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View mPreloader;

    final int NEXT = 1;
    final int PREVIOUS = 2;
    final int SUBMIT = 3;
    final int START = 4;

    //new patched
    TimelinePatchDataRecordDAO timelinePatchDataRecordDAO = new TimelinePatchDataRecordDAO();

    //flag from notification
    String startedFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eod_questions);
        viewPager = (ViewPager)findViewById(R.id.myviewpager);
        myPagerAdapter = new MyPagerAdapter();
        viewPager.setAdapter(myPagerAdapter);
        viewPager.setOffscreenPageLimit(5);
        Log.d(TAG, " map: " + eodQuestionDataRecord);
        EventBus.getDefault().register(this);
        //code to check from notification
        /*startedFrom = getIntent().getStringExtra("STARTED_FROM");
        if(startedFrom!=null)
            Log.d(TAG, "Printing started from: " + startedFrom);

        Log.d(TAG, String.valueOf(startedFrom == "notification"));
        Log.d(TAG, startedFrom);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private class MyPagerAdapter extends PagerAdapter {

        int NumberOfPages = 4;

        @Override
        public int getCount() {
            return NumberOfPages;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            updateActivityInDatabase(position);
            switch (position) {
                case 0:
                    final LinearLayout instructionLayout = new LinearLayout(EODQuestionsActivity.this);
                    instructionLayout.setOrientation(LinearLayout.VERTICAL);
                    LayoutParams instructionLayoutParams = new LayoutParams(
                            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    instructionLayout.setLayoutParams(instructionLayoutParams);
                    //write title one
                    TextView instructionsTitle = new TextView(EODQuestionsActivity.this);
                    instructionsTitle.setTextColor(Color.BLACK);
                    instructionsTitle.setTextSize(18);
                    instructionsTitle.setTypeface(Typeface.DEFAULT_BOLD);
                    instructionsTitle.setText(ApplicationConstants.EOD_QUESTIONS_TITLE_1);
                    instructionsTitle.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    //
                    TextView instructionsWelcome = new TextView(EODQuestionsActivity.this);
                    instructionsWelcome.setTextSize(14);
                    instructionsWelcome.setTypeface(Typeface.DEFAULT);
                    instructionsWelcome.setText("Thank you for being a part of this study! You will" +
                            " now be asked a few questions about your day and your diabetes.");
                    instructionsWelcome.setTextColor(getResources().getColor(R.color.dark_grey));
                    //
                    TextView instructionsSubTitle = new TextView(EODQuestionsActivity.this);
                    instructionsSubTitle.setTextSize(18);
                    instructionsSubTitle.setTypeface(Typeface.DEFAULT_BOLD);
                    instructionsSubTitle.setText("Instructions");
                    instructionsSubTitle.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    //
                    TextView instructionOne = new TextView(EODQuestionsActivity.this);
                    instructionOne.setTextSize(14);
                    instructionOne.setTypeface(Typeface.DEFAULT);
                    instructionOne.setText("1. If you do not know the answer to a question, please " +
                            "leave the answer box blank.");
                    instructionOne.setTextColor(getResources().getColor(R.color.dark_grey));
                    //
                    TextView instructionTwo = new TextView(EODQuestionsActivity.this);
                    instructionTwo.setTextSize(14);
                    instructionTwo.setTypeface(Typeface.DEFAULT);
                    instructionTwo.setText("2. All the answers are confidential and cannot be accessed by anyone" +
                            " other than the researchers.");
                    instructionOne.setTextColor(getResources().getColor(R.color.dark_grey));
                    //
                    TextView instructionThree = new TextView(EODQuestionsActivity.this);
                    instructionThree.setTextSize(14);
                    instructionThree.setTypeface(Typeface.DEFAULT);
                    instructionThree.setText("3. Your answers will not be shared with your parents.");
                    instructionThree.setTextColor(getResources().getColor(R.color.dark_grey));
                    //
                    Button startButton = new Button(EODQuestionsActivity.this);
                    startButton.setText("Start");
                    startButton.setId(R.id.startButton);
                    startButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                    startButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    //
                    LinearLayout.LayoutParams layoutParamsInstructionTitles = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams layoutParamsInstructionText = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParamsInstructionTitles.setMargins(10, 10, 10, 10);
                    layoutParamsInstructionText.setMargins(20, 10, 5, 5);

                    //
                    instructionLayout.addView(instructionsTitle, layoutParamsInstructionTitles);
                    instructionLayout.addView(instructionsWelcome, layoutParamsInstructionText);
                    instructionLayout.addView(instructionsSubTitle, layoutParamsInstructionTitles);
                    instructionLayout.addView(instructionOne, layoutParamsInstructionText);
                    instructionLayout.addView(instructionTwo, layoutParamsInstructionText);
                    instructionLayout.addView(instructionThree, layoutParamsInstructionText);
                    instructionLayout.addView(startButton, layoutParamsInstructionTitles);

                    final int page_zero = position;
                    startButton.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            updateActivityInDatabase(1);
                            //takeScreenshotForView(viewPager);
                            viewPager.setCurrentItem(1);
                            Toast.makeText(EODQuestionsActivity.this,
                                    "Page " + page_zero + " clicked",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                    container.addView(instructionLayout);
                    return instructionLayout;

                case 1:
                    RelativeLayout timelineLayout = new RelativeLayout(EODQuestionsActivity.this);
                    RelativeLayout.LayoutParams timelineLayoutParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    timelineLayout.setLayoutParams(timelineLayoutParams);

                    //missing data title
                    TextView timelineTitleTextView = new TextView(EODQuestionsActivity.this);
                    RelativeLayout.LayoutParams timelineTitleTextViewParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    timelineTitleTextViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    timelineTitleTextView.setId(R.id.timelineTitleText);
                    timelineTitleTextView.setLayoutParams(timelineTitleTextViewParams);
                    timelineTitleTextView.setTextSize(18);
                    timelineTitleTextView.setTypeface(Typeface.DEFAULT_BOLD);
                    timelineTitleTextView.setText("Your Day");
                    timelineTitleTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    //add recycler view
                    mRecyclerView = new RecyclerView(EODQuestionsActivity.this);
                    RelativeLayout.LayoutParams recyclerLayoutParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    recyclerLayoutParams.addRule(RelativeLayout.BELOW, timelineTitleTextView.getId());
                    mRecyclerView.setLayoutParams(recyclerLayoutParams);
                    mRecyclerView.setVerticalScrollBarEnabled(true);
                    mRecyclerView.setScrollBarSize(10);
                    mRecyclerView.setScrollBarFadeDuration(20);
                    mRecyclerView.setId(R.id.recyclerView);

                    mLayoutManager = new LinearLayoutManager(EODQuestionsActivity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mPreloader = new ProgressBar(EODQuestionsActivity.this);
                    mPreloader.setVisibility(View.VISIBLE);

                    //add buttons
                    Button nextButton_ = new Button(EODQuestionsActivity.this);
                    nextButton_.setText("Next");
                    nextButton_.setId(R.id.nextButton);
                    nextButton_.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                    nextButton_.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    RelativeLayout.LayoutParams nextButtonParams_ = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    nextButtonParams_.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    nextButton_.setLayoutParams(nextButtonParams_);

                    Button previousButton_ = new Button(EODQuestionsActivity.this);
                    previousButton_.setText("Back");
                    previousButton_.setId(R.id.prevButton);
                    previousButton_.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                    previousButton_.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    RelativeLayout.LayoutParams previousButtonParams_ = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    previousButtonParams_.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    previousButton_.setLayoutParams(previousButtonParams_);

                    // Fake text view is to give the relative layout a white background color.
                    TextView fakeTextView = new TextView(EODQuestionsActivity.this);
                    RelativeLayout.LayoutParams fakeTextViewParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    fakeTextViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    fakeTextView.setId(R.id.fakeTextView);
                    fakeTextView.setLayoutParams(timelineTitleTextViewParams);
                    fakeTextView.setTextSize(18);
                    fakeTextView.setTypeface(Typeface.DEFAULT_BOLD);
                    fakeTextView.setText("Your Day");
                    fakeTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                    previousButton_.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            viewPager.setCurrentItem(0);
                        }
                    });

                    timelineLayout.addView(timelineTitleTextView);
                    timelineLayout.addView(mRecyclerView);
                    new EODQuestionsActivity.MyPagerAdapter.DataFetchTask().execute();
                    container.addView(timelineLayout);

                    nextButton_.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            viewPager.setCurrentItem(2);
                        }
                    });
                    return timelineLayout;
                case 2:
                    //create a layout
                    LinearLayout layout_one = new LinearLayout(EODQuestionsActivity.this);
                    layout_one.setOrientation(LinearLayout.VERTICAL);
                    LayoutParams layoutOneParams = new LayoutParams(
                            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    layout_one.setLayoutParams(layoutOneParams);

                    //missing data title
                    TextView missingDataTitleTextView = new TextView(EODQuestionsActivity.this);
                    missingDataTitleTextView.setTextSize(18);
                    missingDataTitleTextView.setTypeface(Typeface.DEFAULT_BOLD);
                    missingDataTitleTextView.setText("Your Day");
                    missingDataTitleTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                    //missing data text view
                    TextView missingDataTextView =  new TextView(EODQuestionsActivity.this);
                    missingDataTextView.setTextSize(14);
                    missingDataTextView.setTypeface(Typeface.DEFAULT);
                    missingDataTextView.setText("Was there anything else you wanted to share? (in case there was" +
                            " missing data from the day)");
                    missingDataTextView.setTextColor(getResources().getColor(R.color.dark_grey));

                    //edit text
                    final EditText missingDataEditText = new EditText(EODQuestionsActivity.this);
                    LayoutParams missingDataEditTextParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    missingDataEditText.setLayoutParams(missingDataEditTextParams);
                    missingDataEditText.setHeight(200);

                    //write title two
                    TextView textView_one = new TextView(EODQuestionsActivity.this);
                    textView_one.setTextSize(18);
                    textView_one.setTypeface(Typeface.DEFAULT_BOLD);
                    textView_one.setText(ApplicationConstants.EOD_QUESTIONS_TITLE_2);
                    textView_one.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    //create question two
                    TextView textView_two = new TextView(EODQuestionsActivity.this);
                    textView_two.setTextSize(14);
                    textView_two.setTypeface(Typeface.DEFAULT);
                    textView_two.setText(ApplicationConstants.EOD_QUESTION_TWO_LIFE_EVENTS);
                    textView_two.setTextColor(getResources().getColor(R.color.dark_grey));

                    final EditText editText_one = new EditText(EODQuestionsActivity.this);
                    LayoutParams editTextOneParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    editText_one.setLayoutParams(editTextOneParams);
                    editText_one.setHeight(200);

                    Button nextButton_one = new Button(EODQuestionsActivity.this);
                    nextButton_one.setText("Next");
                    nextButton_one.setId(R.id.nextButton);
                    nextButton_one.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                    nextButton_one.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    RelativeLayout.LayoutParams nextButtonParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    nextButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    nextButton_one.setLayoutParams(nextButtonParams);

                    Button previousButton_one = new Button(EODQuestionsActivity.this);
                    previousButton_one.setText("Back");
                    previousButton_one.setId(R.id.prevButton);
                    previousButton_one.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                    previousButton_one.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    RelativeLayout.LayoutParams previousButtonParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    previousButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    previousButton_one.setLayoutParams(previousButtonParams);

                    RelativeLayout buttonLayout = new RelativeLayout(EODQuestionsActivity.this);
                    LayoutParams buttonLayoutParams = new LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    buttonLayout.setLayoutParams(buttonLayoutParams);

                    buttonLayout.addView(previousButton_one);
                    buttonLayout.addView(nextButton_one);

                    layout_one.addView(missingDataTitleTextView);
                    layout_one.addView(missingDataTextView);
                    layout_one.addView(missingDataEditText);
                    layout_one.addView(textView_one);
                    layout_one.addView(textView_two);
                    layout_one.addView(editText_one);
                    layout_one.addView(buttonLayout);

                    final int page = position;
                    nextButton_one.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String answer_one = missingDataEditText.getText().toString();
                            String answer_two = editText_one.getText().toString();

                            if(answer_one==null || answer_one=="" || answer_one.trim()==""){
                                eodQuestionDataRecord.setEODAnswerOne("");
                            }
                            else {
                                eodQuestionDataRecord.setEODAnswerOne(answer_one);
                            }

                            if(answer_two==null || answer_two=="" || answer_two.trim()==""){
                                eodQuestionDataRecord.setEODAnswerTwo("");
                            }
                            else {
                                eodQuestionDataRecord.setEODAnswerTwo(answer_two);
                            }
                            viewPager.setCurrentItem(3);
                            Toast.makeText(EODQuestionsActivity.this,
                                    "Page " + page + " clicked",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                    previousButton_one.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            viewPager.setCurrentItem(1);
                            Toast.makeText(EODQuestionsActivity.this,
                                    "Page " + page + " clicked",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                    container.addView(layout_one);

                    return layout_one;
                case 3:
                    LinearLayout layout_two = new LinearLayout(EODQuestionsActivity.this);
                    layout_two.setOrientation(LinearLayout.VERTICAL);
                    LayoutParams layoutTwoParams = new LayoutParams(
                            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    layout_two.setLayoutParams(layoutTwoParams);

                    TextView textView_three = new TextView(EODQuestionsActivity.this);
                    textView_three.setTextSize(18);
                    textView_three.setTypeface(Typeface.DEFAULT_BOLD);
                    textView_three.setText(ApplicationConstants.EOD_QUESTIONS_TITLE_3);
                    textView_three.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                    TextView textView_four = new TextView(EODQuestionsActivity.this);
                    textView_four.setTextSize(14);
                    textView_four.setTypeface(Typeface.DEFAULT);
                    textView_four.setText(ApplicationConstants.EOD_QUESTION_THREE_DIABETES_EVENTS);
                    textView_four.setTextColor(getResources().getColor(R.color.dark_grey));

                    final EditText editText_three = new EditText(EODQuestionsActivity.this);
                    LayoutParams editTextThreeParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    editText_three.setLayoutParams(editTextThreeParams);
                    editText_three.setHeight(200);

                    TextView textView_five = new TextView(EODQuestionsActivity.this);
                    textView_five.setTextSize(14);
                    textView_five.setTypeface(Typeface.DEFAULT);
                    textView_five.setText(ApplicationConstants.EOD_QUESTION_FOUR_DIABETES_EVENTS);
                    textView_five.setTextColor(getResources().getColor(R.color.dark_grey));

                    final EditText editText_four = new EditText(EODQuestionsActivity.this);
                    LayoutParams editTextFourParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    editText_four.setLayoutParams(editTextFourParams);
                    editText_four.setHeight(200);

                    Button nextButton_two = new Button(EODQuestionsActivity.this);
                    nextButton_two.setText("Submit");
                    nextButton_two.setId(R.id.submitButton);
                    nextButton_two.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                    nextButton_two.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    RelativeLayout.LayoutParams nextButtonTwoParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    nextButtonTwoParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    nextButton_two.setLayoutParams(nextButtonTwoParams);

                    Button previousButton_two = new Button(EODQuestionsActivity.this);
                    previousButton_two.setText("Back");
                    previousButton_two.setId(R.id.prevButton);
                    previousButton_two.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                    previousButton_two.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    RelativeLayout.LayoutParams previousButtonTwoParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    previousButtonTwoParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    previousButton_two.setLayoutParams(previousButtonTwoParams);

                    RelativeLayout buttonLayoutTwo = new RelativeLayout(EODQuestionsActivity.this);
                    LayoutParams buttonLayoutTwoParams_ = new LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    buttonLayoutTwo.setLayoutParams(buttonLayoutTwoParams_);

                    buttonLayoutTwo.addView(previousButton_two);
                    buttonLayoutTwo.addView(nextButton_two);

                    layout_two.addView(textView_three);
                    layout_two.addView(textView_four);
                    layout_two.addView(editText_three);
                    layout_two.addView(textView_five);
                    layout_two.addView(editText_four);
                    layout_two.addView(buttonLayoutTwo);

                    final int page_ = position;
                    previousButton_two.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            viewPager.setCurrentItem(2);
                            Toast.makeText(EODQuestionsActivity.this,
                                    "Page " + page_ + " clicked",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                    nextButton_two.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            String answer_three = editText_three.getText().toString();
                            String answer_four = editText_four.getText().toString();

                            if(answer_three==null || answer_three=="" || answer_three.trim()==""){
                                eodQuestionDataRecord.setEODAnswerThree("");
                            }
                            else {
                                eodQuestionDataRecord.setEODAnswerThree(answer_three);
                            }

                            if(answer_four==null || answer_four=="" || answer_four.trim()==""){
                                eodQuestionDataRecord.setEODAnswerFour("");
                            }
                            else {
                                eodQuestionDataRecord.setEODAnswerFour(answer_four);
                            }

                            try {
                                eodQuestionAnswerDAO.add(eodQuestionDataRecord);
                                for(Map.Entry<Integer, String> item: mAdapter.patchMap.entrySet()) {
                                    Log.d(TAG, "Adding patches to database");
                                    DataRecord d = mAdapter.mDataSet.get(item.getKey());
                                    timelinePatchDataRecordDAO.add(
                                            new TimelinePatchDataRecord(item.getValue(),
                                                    d.getClass(), d.getCreationTime()));
                                }
                            } catch (DAOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(EODQuestionsActivity.this,
                                    "Adding to database",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                    container.addView(layout_two);
                    return layout_two;
                default:
                    return null;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // We don't want to destroy anything which has answers
            // but the cards layout must be destroyed so that it renders properly.
            if(position == 1) {
                // container.removeView((View)object);
            }
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
                        dataSet.add(new LocationBasedDataRecord() {
                            @Override
                            public String getLocation() {
                                return null;
                            }

                            @Override
                            public long getCreationTime() {
                                return 0;
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
                //mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new TimelineCardsAdapter(dataSet, getApplicationContext());
                mRecyclerView.setAdapter(mAdapter);
                mPreloader.setVisibility(View.GONE);
            }

            private void showFailureContent(Exception e) {
                Log.e(TAG, e.getMessage());
                mPreloader.setVisibility(View.GONE);
                //mFailureContent.setVisibility(View.VISIBLE);
            }
        }

    }

    @Subscribe
    public void goToPageAfterTimeline(TimelineNextPageEvent event) {
        viewPager.setCurrentItem(2);
        Log.d(TAG, "Clicked next: now will set the new patches");
        //go through all the children of mRecycler view
        Log.d(TAG, "number of children of the recycler view: " + mRecyclerView.getChildCount());
        View child;
    }

    @Subscribe
    public void goToPageBeforeTimeline(TimelinePrevPageEvent event) {
        viewPager.setCurrentItem(0);
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if(startedFrom!=null){
            Log.d(TAG, "started from notification");
            //create screenshot and add to db
            Bitmap screenshot = getBitmapOFRootView(findViewById(R.id.diary_linear_layout));
            if(screenshot==null)
                Log.d(TAG, "screenshot is null");
            String base64 = getBase64FromBitmap(screenshot);
            Screenshot screenshotFromBitmap = new Screenshot(base64);
            //add to DB
            Log.d(TAG, "adding screenshot to database");
            String myUserEmail = UserPreferences.getInstance().getPreference(Constants.KEY_ENCODED_EMAIL);
            Firebase dataRecordListRef = new Firebase(Constants.FIREBASE_URL_DIARYSCREENSHOT)
                    .child(myUserEmail)
                    .child(new SimpleDateFormat("MMddyyyy").format(new Date()).toString());
            dataRecordListRef.push().setValue(screenshotFromBitmap);
        }
        else
            Log.d(TAG, "was not started from notification");

    }*/

    /*@Override
    protected void onStart() {
        super.onStart();
        if(startedFrom == "notification"){
            Log.d(TAG, "started from notification");
            //create screenshot and add to db
            Bitmap screenshot = getBitmapOFRootView(viewPager);
            String base64 = getBase64FromBitmap(screenshot);
            Screenshot screenshotFromBitmap = new Screenshot(base64);
            //add to DB
            Log.d(TAG, "adding screenshot to database");
            String myUserEmail = UserPreferences.getInstance().getPreference(Constants.KEY_ENCODED_EMAIL);
            Firebase dataRecordListRef = new Firebase(Constants.FIREBASE_URL_DIARYSCREENSHOT)
                    .child(myUserEmail)
                    .child(new SimpleDateFormat("MMddyyyy").format(new Date()).toString());
            dataRecordListRef.push().setValue(screenshot);
        }
    }*/

    public void updateActivityInDatabase(int viewCount) {
        Firebase firebaseDiaryScreenshotRef = new Firebase(Constants.FIREBASE_URL_DIARYSCREENSHOT)
                .child("Activity")
                .child(UserPreferences
                        .getInstance()
                        .getPreference(Constants.KEY_ENCODED_EMAIL))
                .child(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        firebaseDiaryScreenshotRef.push().setValue(viewCount);
    }

    public void takeScreenshotForView(View view) {
        View layout_one = view.getRootView();
        view.setDrawingCacheEnabled(true);
        String base64 = getBase64FromBitmap(view.getDrawingCache());
        Firebase firebaseDiaryScreenshotRef = new Firebase(Constants.FIREBASE_URL_DIARYSCREENSHOT)
                .child(UserPreferences
                        .getInstance()
                        .getPreference(Constants.KEY_ENCODED_EMAIL))
                .child(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        firebaseDiaryScreenshotRef.push().setValue(base64);
    }

    protected String getBase64FromBitmap(Bitmap b) {
        //Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, 800, 800, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 20, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    public void onDestroy() {
        updateActivityInDatabase(-1);
        super.onDestroy();
    }
}