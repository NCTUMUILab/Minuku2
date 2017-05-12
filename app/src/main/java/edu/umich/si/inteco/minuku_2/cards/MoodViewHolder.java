package edu.umich.si.inteco.minuku_2.cards;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku.model.MoodDataRecord;
import edu.umich.si.inteco.minuku_2.R;
import edu.umich.si.inteco.minuku_2.view.customview.MoodEntryView;
import edu.umich.si.inteco.minukucore.model.DataRecord;
import edu.umich.si.inteco.minukucore.model.LocationBasedDataRecord;

/**
 * Created by neerajkumar on 10/15/16.
 */

public class MoodViewHolder extends ViewHolder {
    Context mContext;
    MoodEntryView moodEntryView;
    EditText moodNotes;
    String TAG = "MoodViewHolder";


    public MoodViewHolder(View v, Context context, TimelineCardsAdapter adapter) {
        super(v, adapter);
        this.mContext = context;
        this.moodEntryView = (MoodEntryView) v.findViewById(R.id.graphCustomView);
        this.moodNotes = (EditText) v.findViewById(R.id.notes_mood);
        moodNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "Setting true for mood card");
                isEdited.setText("true");
                mAdapter.patchMap.put(position, moodNotes.getText().toString());
            }
        });
    }

    @Override
    public void render(LocationBasedDataRecord dataRecord) {
        super.render(dataRecord);
        MoodDataRecord moodDataRecord = (MoodDataRecord) dataRecord;
        this.moodEntryView.setFirstMood(moodDataRecord.getX(), moodDataRecord.getY());
        this.moodEntryView.setBackgroundImageId(getImageForMood(moodDataRecord));
    }

    //this should return a view, Mood Entry View with background as one of the four
    //highlighted quadrants and the value of mood tapped
    private int getImageForMood(DataRecord mood) {
        //return "Energy: " + String.valueOf(((MoodDataRecord)mood).getEnergyLevel());
        float energyLevel = ((MoodDataRecord)mood).getEnergyLevel();
        float moodLevel= ((MoodDataRecord)mood).getMoodLevel();
        if(energyLevel>=0 && moodLevel>=0)
            return (R.drawable.high_positive_1);
        if(energyLevel<=0 && moodLevel>=0)
            return (R.drawable.low_positive_1);
        if(energyLevel>=0 && moodLevel<=0)
            return (R.drawable.high_negative_1);
        if(energyLevel<=0 && moodLevel<=0)
            return (R.drawable.low_negative_1);

        return (R.drawable.graph_new);
    }
}
