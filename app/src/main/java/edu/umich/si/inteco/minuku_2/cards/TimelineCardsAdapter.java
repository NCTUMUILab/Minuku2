package edu.umich.si.inteco.minuku_2.cards;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.si.inteco.minuku.model.MoodDataRecord;
import edu.umich.si.inteco.minuku_2.R;
import edu.umich.si.inteco.minuku_2.model.DiabetesLogDataRecord;
import edu.umich.si.inteco.minukucore.model.DataRecord;
import edu.umich.si.inteco.minukucore.model.LocationBasedDataRecord;

/**
 * Created by neerajkumar on 10/14/16.
 */

public class TimelineCardsAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "TimelineCardsAdapter";
    public Map<Integer, String> patchMap = new HashMap<>();

    public List<LocationBasedDataRecord> mDataSet;
    Context mContext;

    private static int MOOD_CARD = 0;
    private static int DIARY_CARD = 1;
    private static int BUTTON_CARD = 2;

    public TimelineCardsAdapter(List<LocationBasedDataRecord> aDataSet, Context context) {
        mDataSet = aDataSet;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == MOOD_CARD) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.mood_data_card, viewGroup, false);
            return new MoodViewHolder(v, mContext, this);
        } else if (viewType == DIARY_CARD) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.diary_log_data_card, viewGroup, false);
            return new DiaryLogViewHolder(v, this);
        } else {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.buttons_view_card, viewGroup, false);
            return new ButtonsViewHolder(v, mContext, this);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == MOOD_CARD) {
            MoodViewHolder holder = (MoodViewHolder) viewHolder;
            holder.render(mDataSet.get(position));
            holder.setPosition(position);
        } else if (viewHolder.getItemViewType() == DIARY_CARD) {
            DiaryLogViewHolder holder = (DiaryLogViewHolder) viewHolder;
            holder.render(mDataSet.get(position));
            holder.setPosition(position);
        } else {
            ButtonsViewHolder holder = (ButtonsViewHolder) viewHolder;
            holder.render(mDataSet.get(position));
            holder.setPosition(position);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mDataSet.get(position) instanceof MoodDataRecord) {
            return MOOD_CARD;
        } else if(mDataSet.get(position) instanceof DiabetesLogDataRecord){
            return DIARY_CARD;
        } else {
            return BUTTON_CARD;
        }
    }
}
