package edu.umich.si.inteco.minuku_2;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku_2.dao.PromptMissedReportsQnADAO;
import edu.umich.si.inteco.minuku_2.model.PromptMissedReportsQnADataRecord;
import edu.umich.si.inteco.minukucore.dao.DAOException;

/**
 * Created by neerajkumar on 11/8/16.
 */

public class PromptMissedDataQuesttionaireActivity extends  BaseActivity {

    ViewPager mViewPager;
    ListView mcqQuestionList;
    EditText freeTextResponse;

    Set<Integer> selectedAnswers = new HashSet<>(5);
    private ArrayAdapter<AnswerChoice> listAdapter ;
    List<String> freeTextAnswer = new ArrayList<>();
    List<String> mcqAnswers = new ArrayList<>();
    List<String> radioButtonAnswer = new ArrayList<>();

    PromptMissedReportsQnADAO promptMissedReportsQnADAO = new PromptMissedReportsQnADAO();

    //create all data records
    PromptMissedReportsQnADataRecord pageOneData;
    PromptMissedReportsQnADataRecord pageTwoData;
    PromptMissedReportsQnADataRecord pageThreeData;
    PromptMissedReportsQnADataRecord pageFourData;

    String TAG = "PromptMissedDataQuesttionaireActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missed_data_prompt_activity);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new QuestionsPagerAdapter(this));
    }

    public void onRadioButtonClicked(View view) {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_button);
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.forgot_to_log:
                if (checked) {
                    String textForCheckedRadioButton = ((RadioButton)findViewById(R.id.forgot_to_log)).getText().toString();
                    radioButtonAnswer.add(textForCheckedRadioButton);
                    pageOneData = new PromptMissedReportsQnADataRecord((
                            (TextView) findViewById(R.id.pageOneQuestion)).getText().toString());
                    pageOneData.setAnswer(radioButtonAnswer);

                    mViewPager.setCurrentItem(3);

                    /*String textForCheckedRadioButton = ((RadioButton)findViewById(R.id.forgot_to_log)).getText().toString();
                    radioButtonAnswer.add(textForCheckedRadioButton);
                    pageOneData = new PromptMissedReportsQnADataRecord((
                            (TextView) findViewById(R.id.pageOneQuestion)).getText().toString());
                    pageOneData.setAnswer(radioButtonAnswer);


                    mcqQuestionList = (ListView) findViewById(R.id.prompt_missed_reason_mcq);

                    ArrayList<AnswerChoice> list = new ArrayList<>();
                    list.add(new AnswerChoice("TestA"));
                    list.add(new AnswerChoice("TestB"));

                    // Set our custom array adapter as the ListView's adapter.
                    listAdapter = new AnswerArrayAdapter(this, list);
                    mcqQuestionList.setAdapter( listAdapter );


                    mcqQuestionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view,
                                                int position, long l) {
                            Log.d(TAG,"I clicked MCQ choice");
                            AnswerChoice answerChoice = listAdapter.getItem(position);
                            answerChoice.toggleChecked();
                            AnswerRowHolder viewHolder = (AnswerRowHolder) view.getTag();
                            viewHolder.getCheckBox().setChecked(answerChoice.isChecked());
                            if(answerChoice.isChecked()) {
                                Log.d(TAG,"I am now going to add answer");
                                selectedAnswers.add(position);
                                Log.d(TAG, "mcq answer added: " + viewHolder.getTextView().getText().toString());
                                mcqAnswers.add(viewHolder.getTextView().getText().toString());
                            } else {
                                selectedAnswers.remove(position);
                            }
                        }
                    });
                    pageTwoData = new PromptMissedReportsQnADataRecord((
                            (TextView) findViewById(R.id.pageTwoQuestion)).getText().toString());
                    pageTwoData.setAnswer(mcqAnswers);*/
                }
                break;
            case R.id.forgot_care_activities:
                if (checked) {
                    String textForCheckedRadioButton = ((RadioButton)findViewById(R.id.forgot_care_activities)).getText().toString();
                    radioButtonAnswer.add(textForCheckedRadioButton);
                    pageOneData = new PromptMissedReportsQnADataRecord((
                            (TextView) findViewById(R.id.pageOneQuestion)).getText().toString());
                    pageOneData.setAnswer(radioButtonAnswer);

                    mViewPager.setCurrentItem(2);
                }
                break;
        }
    }

    public void goBackToFirstPage(View v) {
        mViewPager.setCurrentItem(0);
    }

    public void goBackToSecondPage(View v) {
        mViewPager.setCurrentItem(1);
    }

    public void acceptMCQAnswerAndFinishActivity(View v) {
        // add to DAO here.
        try {
            promptMissedReportsQnADAO.add(pageOneData);
            promptMissedReportsQnADAO.add(pageTwoData);
        } catch (DAOException e) {
            e.printStackTrace();
        }
        finish();
    }

    public void acceptFreeTextAnswerMissingLog(View v) {
        // add to DAO here.
        freeTextResponse = (EditText) findViewById(R.id.prompt_missedlog_reason_free_text);
        freeTextAnswer.add(freeTextResponse.getText().toString().trim());
        pageFourData = new PromptMissedReportsQnADataRecord((
                (TextView) findViewById(R.id.pageFourQuestion)).getText().toString());
        pageFourData.setAnswer(freeTextAnswer);
        //add to DAO here.
        try {
            promptMissedReportsQnADAO.add(pageOneData);
            promptMissedReportsQnADAO.add(pageFourData);
        } catch (DAOException e) {
            e.printStackTrace();
        }

        finish();
    }

    public void acceptFreeTextAnswerMissingActivity(View v) {
        freeTextResponse = (EditText) findViewById(R.id.prompt_missedactivity_reason_free_text);
        freeTextAnswer.add(freeTextResponse.getText().toString().trim());
        pageThreeData = new PromptMissedReportsQnADataRecord((
                (TextView) findViewById(R.id.pageThreeQuestion)).getText().toString());
        pageThreeData.setAnswer(freeTextAnswer);
        //add to DAO here.
        try {
            promptMissedReportsQnADAO.add(pageOneData);
            promptMissedReportsQnADAO.add(pageThreeData);
        } catch (DAOException e) {
            e.printStackTrace();
        }

        finish();
    }

    /** Holds answer choice data. */
    private static class AnswerChoice {
        private String name = "" ;
        private boolean checked = false ;
        public AnswerChoice() {}
        public AnswerChoice( String name ) {
            this.name = name ;
        }
        public AnswerChoice( String name, boolean checked ) {
            this.name = name ;
            this.checked = checked ;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public boolean isChecked() {
            return checked;
        }
        public void setChecked(boolean checked) {
            this.checked = checked;
        }
        public String toString() {
            return name ;
        }
        public void toggleChecked() {
            checked = !checked ;
        }
    }

    /** Holds child views for one row. */
    private static class AnswerRowHolder {
        private CheckBox checkBox ;
        private TextView textView ;
        public AnswerRowHolder() {}
        public AnswerRowHolder( TextView textView, CheckBox checkBox ) {
            this.checkBox = checkBox ;
            this.textView = textView ;
        }
        public CheckBox getCheckBox() {
            return checkBox;
        }
        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }
        public TextView getTextView() {
            return textView;
        }
        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    /** Custom adapter for displaying an array of Answer objects. */
    private static class AnswerArrayAdapter extends ArrayAdapter<AnswerChoice> {

        private LayoutInflater inflater;

        public AnswerArrayAdapter(Context context, List<AnswerChoice> planetList ) {
            super( context, R.layout.row, R.id.textView1, planetList );
            // Cache the LayoutInflate to avoid asking for a new one each time.
            inflater = LayoutInflater.from(context) ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Planet to display
            AnswerChoice planet = (AnswerChoice) this.getItem( position );

            // The child views in each row.
            CheckBox checkBox ;
            TextView textView ;

            // Create a new row view
            if ( convertView == null ) {
                convertView = inflater.inflate(R.layout.row, null);

                // Find the child views.
                textView = (TextView) convertView.findViewById( R.id.textView1 );
                checkBox = (CheckBox) convertView.findViewById( R.id.checkBox1 );

                // Optimization: Tag the row with it's child views, so we don't have to
                // call findViewById() later when we reuse the row.
                convertView.setTag( new AnswerRowHolder(textView,checkBox) );

                // If CheckBox is toggled, update the planet it is tagged with.
                checkBox.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        AnswerChoice answer = (AnswerChoice) cb.getTag();
                        answer.setChecked( cb.isChecked() );
                    }
                });
            }
            // Reuse existing row view
            else {
                // Because we use a ViewHolder, we avoid having to call findViewById().
                AnswerRowHolder viewHolder = (AnswerRowHolder) convertView.getTag();
                checkBox = viewHolder.getCheckBox() ;
                textView = viewHolder.getTextView() ;
            }

            // Tag the CheckBox with the Planet it is displaying, so that we can
            // access the planet in onClick() when the CheckBox is toggled.
            checkBox.setTag( planet );

            // Display planet data
            checkBox.setChecked( planet.isChecked() );
            textView.setText( planet.getName() );

            return convertView;
        }

    }

}
