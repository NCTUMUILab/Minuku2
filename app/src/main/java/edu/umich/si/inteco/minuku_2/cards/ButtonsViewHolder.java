package edu.umich.si.inteco.minuku_2.cards;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;

import edu.umich.si.inteco.minuku_2.R;
import edu.umich.si.inteco.minuku_2.event.TimelineNextPageEvent;
import edu.umich.si.inteco.minuku_2.event.TimelinePrevPageEvent;
import edu.umich.si.inteco.minuku_2.view.customview.MoodEntryView;
import edu.umich.si.inteco.minukucore.model.LocationBasedDataRecord;

/**
 * Created by shriti on 10/29/16.
 */

public class ButtonsViewHolder extends ViewHolder {

    Context mContext;
    Button nextButton;
    Button previousButton;

    public ButtonsViewHolder(View v, Context context, TimelineCardsAdapter adapter) {
        super(v, adapter);
        this.mContext = context;
        this.nextButton = (Button) v.findViewById(R.id.nextButton);
        this.previousButton = (Button) v.findViewById(R.id.previousButton);

        this.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new TimelineNextPageEvent());
            }
        });

        this.previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new TimelinePrevPageEvent());
            }
        });
    }

    @Override
    public void render(LocationBasedDataRecord dataRecord) {
        super.render(dataRecord);
    }
}
