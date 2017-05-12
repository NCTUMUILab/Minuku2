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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.desmond.squarecamera.CameraActivity;
import com.desmond.squarecamera.ImageUtility;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku.manager.MinukuStreamManager;
import edu.umich.si.inteco.minuku.model.DataRecordUtil;
import edu.umich.si.inteco.minuku.model.UserSubmissionStats;
import edu.umich.si.inteco.minuku.tags.Model;
import edu.umich.si.inteco.minuku_2.manager.InstanceManager;
import edu.umich.si.inteco.minuku_2.model.DiabetesLogDataRecord;
import edu.umich.si.inteco.minukucore.exception.StreamNotFoundException;
import edu.umich.si.inteco.minukucore.streamgenerator.StreamGenerator;
import me.gujun.android.taggroup.TagGroup;

/**
 * Created by shriti on 8/20/16.
 */
public class DiabetesLogActivity extends BaseActivity {

    private String TAG = "DiabetesLogActivity";

    private ImageView glucoseReadingImage;
    private ImageView foodImage;
    protected String glucoseReadingImageBase64ImageData;
    protected String foodImageBase64ImageData;
    public ImageView tempImageView;
    private String tempBase64ImageData;

    protected static final int REQUEST_CAMERA = 101;
    protected static final int REQUEST_GALLERY = 102;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private EditText mBGNumberInput;
    private EditText mCarbsConsumedInput;
    private EditText mBasalInsulinInput;
    private EditText mBolusInsulinInput;

    private EditText mNoteData;
    private Button acceptButton;
    private Button rejectButton;

    private DiabetesLogDataRecord diabetesLogDataRecord;
    private String imageTypeFlag;

    private Context mContext;
    private UserSubmissionStats userSubmissionStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_diabetes_log_activity);
        mContext = getApplicationContext();

         userSubmissionStats = InstanceManager
                .getInstance(mContext).getUserSubmissionStats();

        glucoseReadingImage = (ImageView) findViewById(R.id.blood_glucose);
        foodImage = (ImageView) findViewById(R.id.food);

        //Add click listeners
        glucoseReadingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "camera started");
                tempImageView = glucoseReadingImage;
                glucoseReadingImageBase64ImageData ="";
                imageTypeFlag = "GLUCOSE_READING";
                requestPermission();
                Log.d(TAG, "returning glucose reading with a value of : " + glucoseReadingImageBase64ImageData);
                /*if(glucoseReadingImageBase64ImageData!=null)
                    userSubmissionStats.incrementGlucoseReadingCount();*/
            }
        });
        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "camera started");
                tempImageView = foodImage;
                foodImageBase64ImageData = "";
                imageTypeFlag = "FOOD";
                requestPermission();
                Log.d(TAG, "returning food with a value of : " + foodImageBase64ImageData);
                if(foodImageBase64ImageData!=null)
                    userSubmissionStats.incrementFoodCount();

            }
        });

        mBGNumberInput = (EditText) findViewById(R.id.edit_bg_number);
        mCarbsConsumedInput = (EditText) findViewById(R.id.edit_carb_count);
        mBasalInsulinInput = (EditText) findViewById(R.id.edit_insulin_basal);
        mBolusInsulinInput = (EditText) findViewById(R.id.edit_insulin_bolus);

        mNoteData = (EditText) findViewById(R.id.note_data);

        TagGroup mTagGroup = (TagGroup) findViewById(R.id.tag_group);
        mTagGroup.setTags(Model.getInstance().getRelevantTags());
        mTagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                mNoteData.append(" #" + tag + " ");
            }
        });

        // Add click listeners for buttons
        acceptButton = (Button) findViewById(R.id.acceptButton);
        rejectButton = (Button) findViewById(R.id.rejectButton);

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

        Bundle extras = getIntent().getExtras();
        if(extras !=null && extras.getBoolean("FROM_NOTIFICATION")) {
            for(String key:extras.keySet()) {
                Log.d("DiabetesLogActivity", key);
            }
            showToast("This screen was started from a notification with type:" + getIntent().getExtras().getString("PROMPT_TYPE", ""));
        }
    }


    /**
     * This is called when the user pressed "Tick" button on the screen.
     */
    public void acceptResults() {
        //get bg number input
        String bgNumber = mBGNumberInput.getText().toString();
        int intValueOfBgNumber;
        if (bgNumber==null || bgNumber.trim().isEmpty()) {
            intValueOfBgNumber = 0;
        } else {
            intValueOfBgNumber = Integer.valueOf(bgNumber);
        }
        //if either text or image has some data then increment glucose count
        if(glucoseReadingImageBase64ImageData!=null || intValueOfBgNumber!=0)
            userSubmissionStats.incrementGlucoseReadingCount();
        //get the insulin and carbs input
        String carbsConsumed = mCarbsConsumedInput.getText().toString();
        float floatValueOfCarbsConsumed;
        if (carbsConsumed==null || carbsConsumed.trim().isEmpty()) {
            floatValueOfCarbsConsumed = 0.0f;
        } else {
            floatValueOfCarbsConsumed = Float.valueOf(carbsConsumed);
        }

        String basalInsulin = mBasalInsulinInput.getText().toString();
        float floatValueOfBasalInsulin;
        if (basalInsulin==null || basalInsulin.trim().isEmpty()) {
            floatValueOfBasalInsulin = 0.0f;
        } else {
            floatValueOfBasalInsulin = Float.valueOf(basalInsulin);
        }

        String bolusInsulin = mBolusInsulinInput.getText().toString();
        float floatValueOfBolusInsulin;
        if (bolusInsulin==null || bolusInsulin.trim().isEmpty()){
            floatValueOfBolusInsulin = 0.0f;
        } else {
            floatValueOfBolusInsulin = Float.valueOf(bolusInsulin);
            userSubmissionStats.incrementInsulinCount();
        }

        // Make sure that there is some data entered by the user in the note field.
        String noteData = mNoteData.getText().toString();

        for(String hashTag: extractAllHashTags(noteData)) {
            Model.getInstance().incrementTagCount(hashTag);
        }

        if (noteData == null || noteData.trim().isEmpty()) {
            noteData = "";
        }

        diabetesLogDataRecord = new DiabetesLogDataRecord(glucoseReadingImageBase64ImageData,
                foodImageBase64ImageData,
                intValueOfBgNumber,
                floatValueOfCarbsConsumed,
                floatValueOfBasalInsulin,
                floatValueOfBolusInsulin,
                noteData,
                DataRecordUtil.attemptToGetSemanticOrNormalLocation());

        try {
            StreamGenerator streamGenerator = MinukuStreamManager.getInstance().
                    getStreamGeneratorFor(DiabetesLogDataRecord.class);
            Log.d(TAG, "Saving results to the database");
            streamGenerator.offer(diabetesLogDataRecord);
        } catch (StreamNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "The note stream does not exist on this device.");
        }

        InstanceManager.getInstance(mContext).setUserSubmissionStats(userSubmissionStats);

        showToast("Your log has been recorded");
        finish();
    }

    /**
     * This is called when the user presses the "X" button the screen.
     */
    public void rejectResults() {
        showToast("Going back to home screen");
        finish();
    }

    private List<String> extractAllHashTags(String str) {
        Pattern MY_PATTERN = Pattern.compile("#(\\S+)");
        Matcher mat = MY_PATTERN.matcher(str);
        List<String> hashTags = new ArrayList<String>();
        while (mat.find()) {
            hashTags.add(mat.group(1));
        }
        return hashTags;
    }

    protected String getBase64FromBitmap(Bitmap b) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, 800, 800, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Log.d(TAG, "Camera result returned isn't OK.");
            finish();
        }
        // There are two types of media that are requested, either from Camera, or from Gallery.
        //Each of them have their own ways of rendering the selected/taken image.
        if (requestCode == REQUEST_IMAGE_CAPTURE
                && resultCode == RESULT_OK
                && null != data) {
            /**Uri photoUri = data.getData();
            Bitmap bitmap = ImageUtility.decodeSampledBitmapFromPath(photoUri.getPath(), 300, 300);**/
            Log.d(TAG, "Camera result returned is OK.");
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
            tempImageView.setImageBitmap(newBitmap);
            tempBase64ImageData = getBase64FromBitmap(bitmap);
        }
        if(imageTypeFlag == "GLUCOSE_READING"){
            Log.d(TAG, "Setting glucose reading image to " + tempBase64ImageData);
            glucoseReadingImageBase64ImageData = tempBase64ImageData;
            Log.d(TAG, "Set glucose reading image to " + glucoseReadingImageBase64ImageData);

        }
        if(imageTypeFlag == "FOOD"){
            Log.d(TAG, "Setting food image to " + tempBase64ImageData);
            foodImageBase64ImageData = tempBase64ImageData;
            Log.d(TAG, "Set food image to " + foodImageBase64ImageData);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void requestPermission() {
        mLayout = findViewById(R.id.main_layout);

        Log.d(TAG, "Requesting camera permission");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG,
                    "Displaying camera permission rationale to provide additional context.");

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_IMAGE_CAPTURE);
        } else {
            Log.d(TAG, "Permission for camera activity already granted. Starting activity.");
            /**Intent startCustomCameraIntent = new Intent(this, CameraActivity.class);
            startActivityForResult(startCustomCameraIntent, REQUEST_IMAGE_CAPTURE);**/
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //intent.putExtra("android.intent.extra.quickCapture",true);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "On request permission result called.");
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /**Intent startCustomCameraIntent = new Intent(this, CameraActivity.class);
                    startActivityForResult(startCustomCameraIntent, REQUEST_CAMERA);**/
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //intent.putExtra("android.intent.extra.quickCapture",true);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                    Log.d(TAG, "Successfully received permissions and started camera activity.");
                } else {
                    finish();
                }
                return;
        }
    }

}
