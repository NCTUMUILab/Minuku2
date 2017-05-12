package edu.umich.si.inteco.minuku_2.cards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku_2.R;
import edu.umich.si.inteco.minuku_2.model.DiabetesLogDataRecord;
import edu.umich.si.inteco.minukucore.model.DataRecord;
import edu.umich.si.inteco.minukucore.model.LocationBasedDataRecord;

/**
 * Created by neerajkumar on 10/15/16.
 */

public class DiaryLogViewHolder extends ViewHolder {
    String TAG = "DiaryLogViewHolder";
    ImageView glucoseReading;
    ImageView food;
    TextView bg;
    TextView carbs;
    TextView basalInsulin;
    TextView bolusInsulin;
    EditText notes;

    public DiaryLogViewHolder(View v, TimelineCardsAdapter adapter) {
        super(v, adapter);
        Log.d(TAG, "constructor in view holder");
        this.glucoseReading = (ImageView) v.findViewById(R.id.blood_glucose_image);
        this.food = (ImageView) v.findViewById(R.id.food_image);
        this.bg = (TextView) v.findViewById(R.id.bg);
        this.carbs = (TextView) v.findViewById(R.id.carbohydrates);
        this.basalInsulin = (TextView) v.findViewById(R.id.basal_insulin);
        this.bolusInsulin = (TextView) v.findViewById(R.id.bolus_insulin);
        this.notes = (EditText) v.findViewById(R.id.notes);
    }

    @Override
    public void render(LocationBasedDataRecord dataRecord) {
        super.render(dataRecord);
        Log.d(TAG, "rendering in view holder");
        if(getBase64DataForGlucoseReading(dataRecord) == null)
            glucoseReading.setVisibility(View.INVISIBLE);
        else {
            Log.d(TAG, "1");
            glucoseReading.setImageBitmap(getBase64DataForGlucoseReading(dataRecord));
        }

        if(getBase64DataForFood(dataRecord) == null)
            food.setVisibility(View.INVISIBLE);
        else {
            Log.d(TAG, "2");
            food.setImageBitmap(getBase64DataForFood(dataRecord));
        }

        if(getTextForBG(dataRecord) == null)
            bg.setText("-");
        else {
            Log.d(TAG, "3");
            bg.setText(getTextForBG(dataRecord));
        }

        if(getTextForCarbs(dataRecord) == null)
            carbs.setText("-");
        else {
            Log.d(TAG, "4");
            carbs.setText(getTextForCarbs(dataRecord));
        }

        if(getTextForBasalInsulin(dataRecord) == null)
            basalInsulin.setText("-");
        else {
            Log.d(TAG, "5");
            basalInsulin.setText(getTextForBasalInsulin(dataRecord));
        }

        if(getTextForBolusInsulin(dataRecord)== null)
            bolusInsulin.setText("-");
        else {
            Log.d(TAG, "6");
            bolusInsulin.setText(getTextForBolusInsulin(dataRecord));
        }

        if(getTextForNotes(dataRecord) == null) {
            //notes.setVisibility(View.INVISIBLE);
            notes.setHint("Add notes here");
        }
        else {
            Log.d(TAG, "7");
            notes.setText(getTextForNotes(dataRecord));
        }

        notes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "Setting true for diary log card");
                isEdited.setText("true");
                //mAdapter.patchMap.put()
                mAdapter.patchMap.put(position, notes.getText().toString());
            }
        });
    }

    private Bitmap getBase64DataForGlucoseReading(DataRecord dataRecord) {
        if(((DiabetesLogDataRecord)dataRecord).getGlucoseImageBase64Data()!=null) {
            Bitmap bitmap = getBitmapFromBase64(((DiabetesLogDataRecord)dataRecord).getGlucoseImageBase64Data());
            return bitmap;
        }
        else {
            return null;
        }
    }

    private Bitmap getBase64DataForFood(DataRecord dataRecord) {
        if(((DiabetesLogDataRecord)dataRecord).getFoodImageBase64Data()!=null) {
            Bitmap bitmap = getBitmapFromBase64(((DiabetesLogDataRecord)dataRecord).getFoodImageBase64Data());
            return bitmap;
        }
        else {
            return null;
        }
    }

    private String getTextForNotes(DataRecord dataRecord) {
        return ((DiabetesLogDataRecord)dataRecord).getNote();
    }

    private String getTextForCarbs(DataRecord dataRecord) {
        Float carbs = ((DiabetesLogDataRecord)dataRecord).getCarbsConsumed();
        if(carbs!=null)
            return carbs.toString();
        else
            return null;
    }

    private String getTextForBasalInsulin(DataRecord dataRecord) {
        Float basal = ((DiabetesLogDataRecord)dataRecord).getBasalInsulin();
        if(basal!=null)
            return basal.toString();
        else
            return null;
    }

    private String getTextForBolusInsulin(DataRecord dataRecord) {
        Float bolus = ((DiabetesLogDataRecord)dataRecord).getBolusInsulin();
        if(bolus!=null)
            return bolus.toString();
        else
            return null;
    }

    private String getTextForBG(DataRecord dataRecord) {
        Integer bgNumber = ((DiabetesLogDataRecord)dataRecord).getBgNumber();
        if(bg!=null)
            return bgNumber.toString();
        else
            return null;
    }

    private Bitmap getBitmapFromBase64(String base64Data) {
        byte[] decodedString = Base64.decode(base64Data, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Bitmap newBitmap = Bitmap.createScaledBitmap(decodedByte, 300, 300, false);
        return newBitmap;

    }
}
