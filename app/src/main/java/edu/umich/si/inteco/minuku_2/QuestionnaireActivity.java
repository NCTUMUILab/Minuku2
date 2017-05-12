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

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.github.dkharrat.nexusdialog.FormController;
import com.github.dkharrat.nexusdialog.FormElementController;
import com.github.dkharrat.nexusdialog.controllers.FormSectionController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umich.si.inteco.minuku.config.Constants;
import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku.manager.MinukuStreamManager;
import edu.umich.si.inteco.minuku.manager.QuestionManager;
import edu.umich.si.inteco.minuku.model.UserSubmissionStats;
import edu.umich.si.inteco.minuku_2.manager.InstanceManager;
import edu.umich.si.inteco.minuku_2.question.QuestionConfig;
import edu.umich.si.inteco.minukucore.exception.QuestionNotFoundException;
import edu.umich.si.inteco.minukucore.exception.StreamNotFoundException;
import edu.umich.si.inteco.minukucore.model.question.FreeResponse;
import edu.umich.si.inteco.minukucore.model.question.MultipleChoice;
import edu.umich.si.inteco.minukucore.model.question.Question;

/**
 * Created by shriti on 7/28/16.
 */
public class QuestionnaireActivity<T extends Question> extends BaseActivity {

    private FormController formController;

    private ImageView acceptButton;
    private ImageView rejectButton;
    
    private Map<T, FormElementController> questionControllerMap;
    private String TAG = "QuestionnaireActivity";

    //FreeResponse q1= new FreeResponse("First Name");
    //FreeResponse q2= new FreeResponse("Last Name");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "creating activity");

        setContentView(R.layout.custom_form);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        setupForm(getIntent().getExtras());

        acceptButton = (ImageView) findViewById(R.id.acceptButton);
        rejectButton = (ImageView) findViewById(R.id.rejectButton);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    acceptResults(getIntent().getExtras());
                } catch (StreamNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectResults();
            }
        });
        questionControllerMap = QuestionManager.getInstance().getQuestionFormControllerMap();
    }

    protected void setupForm(Bundle savedInstanceState) {
        formController = new FormController(this);

        String bundledQuestionnarieId =
                savedInstanceState.getString(Constants.BUNDLE_KEY_FOR_QUESTIONNAIRE_ID);
        // If a questionnaire ID was not provided, then default to the first questionnaire.
        // This does happen when sometimes a notification is dismissed (i.e. cancalled) but
        // still keeps showing up on the status bar and the user clicks on it.
        if(bundledQuestionnarieId == null) {
            bundledQuestionnarieId = "1";
        }
        int questionnaireId = Integer.valueOf(bundledQuestionnarieId);

        Log.d(TAG, "creating form for questionnaire ID " + questionnaireId);
        Log.d(TAG, "prompt source: " + savedInstanceState.getString(Constants.BUNDLE_KEY_FOR_NOTIFICATION_SOURCE));
        //get questionnaire ID from bundle
        //QuestionManager.getInstance().getQuestionnaireForID(ID)
        //
        FormSectionController section = new FormSectionController(this, "Please answer the following questions");

        for(Question q : QuestionManager.getInstance().getQuestionnaireForID(questionnaireId).getQuestionnaire()) {
            try {
                Log.d(TAG, "a question was added to form: "  + q.getQuestion());
                section.addElement(QuestionConfig.getControllerFor(q, getApplicationContext()));
            } catch (QuestionNotFoundException e) {
                Log.d(TAG, "A question passed to question config was not found");
                e.printStackTrace();

            }
        }

        formController.addSection(section);

        ViewGroup containerView = (ViewGroup)findViewById(R.id.form_elements_container);
        formController.recreateViews(containerView);
        Log.d(TAG, "creating the form");

    }

    public void acceptResults(Bundle savedInstanceState) throws StreamNotFoundException {

        //Object firstName = formController.getModel().getValue();
        //Object lastName = formController.getModel().getValue("lastName");

        int questionnaireId = Integer.valueOf(savedInstanceState.getString(Constants.BUNDLE_KEY_FOR_QUESTIONNAIRE_ID));
        Log.d(TAG, "accepting result for questionnaire: " + questionnaireId);

        //for (Map.Entry<T, FormElementController> entry:questionControllerMap.entrySet()) {
        for(Question question : QuestionManager.getInstance().getQuestionnaireForID(questionnaireId).getQuestionnaire()) {
            //T question = entry.getKey();
            Log.d(TAG, "getting the answer for question: " + question.getQuestion());
            Object answer = formController.getModel().getValue(String.valueOf(question.getID()));
            if(question instanceof FreeResponse) {
                Log.d(TAG, "question is a FreeResponse");
                if(answer!=null)
                    ((FreeResponse) question).setAnswer(answer.toString());
                else
                    ((FreeResponse) question).setAnswer("");
                MinukuStreamManager.getInstance()
                            .getStreamGeneratorFor(FreeResponse.class)
                            .offer((FreeResponse) question);
            }
            if(question instanceof MultipleChoice) {
                Log.d(TAG, "question is a MCQ");
                Set<String> answerSet = new HashSet<>();
                if(answer != null) {
                    answerSet = (HashSet<String>) answer;
                    Log.d(TAG, answer.toString());
                }

                List<Integer> answers = new ArrayList<Integer>();

                MultipleChoice mcq = (MultipleChoice) question;
                List<String> answerChoices = Arrays.asList(mcq.getLabels());

                for(String someAnswer: answerSet) {
                    answers.add(answerChoices.indexOf(someAnswer));
                }
                ((MultipleChoice) question).setSelectedAnswerValues(answers.toArray(new Integer[0]));
                MinukuStreamManager.getInstance()
                        .getStreamGeneratorFor(MultipleChoice.class)
                        .offer((MultipleChoice) question);
            }
        }
        Log.d(TAG, "Increasing question count in submission stats");
        UserSubmissionStats userSubmissionStats = InstanceManager
                .getInstance(getApplicationContext())
                .getUserSubmissionStats();
        userSubmissionStats.incrementQuestionCount();
        InstanceManager
                .getInstance(getApplicationContext())
                .setUserSubmissionStats(userSubmissionStats);

        showToast("Your answer has been recorded");
        finish();
    }

    /**
     * This is called when the user presses the "X" button the screen.
     */
    public void rejectResults() {
        showToast("Going back to home screen");
        finish();
    }
}
