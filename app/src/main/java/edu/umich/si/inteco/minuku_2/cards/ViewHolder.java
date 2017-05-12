package edu.umich.si.inteco.minuku_2.cards;

import edu.umich.si.inteco.minuku_2.R;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.umich.si.inteco.minukucore.model.DataRecord;
import edu.umich.si.inteco.minukucore.model.LocationBasedDataRecord;

/**
 * Created by neerajkumar on 10/15/16.
 */

public abstract class ViewHolder extends RecyclerView.ViewHolder {

    TextView locationLabel;
    TextView timeLabel;
    TextView isEdited;
    TimelineCardsAdapter mAdapter;
    Integer position;

    public ViewHolder(View v, TimelineCardsAdapter adapter) {
        super(v);
        this.locationLabel = (TextView)
                v.findViewById(R.id.location_label);
        this.timeLabel = (TextView) v.findViewById(R.id.time_label);
        this.isEdited = (TextView) v.findViewById(R.id.is_edited);
        this.mAdapter = adapter;
    }

    public void render(LocationBasedDataRecord dataRecord) {
        this.locationLabel.setText(
                dataRecord.getLocation() == "" ? "Unknown Location" : dataRecord.getLocation());
        this.timeLabel.setText(getDate(dataRecord.getCreationTime(), "MM/dd hh:mm a"));
    }

    /**
     * Copyright: http://stackoverflow.com/questions/7953725/how-to-convert-milliseconds-to-date-format-in-android
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public void setPosition(int aPosition) {
        this.position = aPosition;
    }
}
