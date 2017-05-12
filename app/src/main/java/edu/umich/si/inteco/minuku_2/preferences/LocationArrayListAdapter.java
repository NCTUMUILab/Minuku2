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

package edu.umich.si.inteco.minuku_2.preferences;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.umich.si.inteco.minuku.config.LocationPreference;
import edu.umich.si.inteco.minuku.config.SelectedLocation;
import edu.umich.si.inteco.minuku_2.R;

/**
 * Created by shriti on 8/16/16.
 */
public class LocationArrayListAdapter extends ArrayAdapter<SelectedLocation> {

    public final Context context;
    public final List<SelectedLocation> items;


    public LocationArrayListAdapter(Context context, List<SelectedLocation> objects) {
        super(context, R.layout.listitem_locationpreference, objects);
        this.context = context;
        this.items = objects;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.listitem_locationpreference, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.firstLine);
        TextView txtSubTitle = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(LocationPreference.getInstance().getLocations().get(position));
                LocationPreference.getInstance().deleteLocation(position);
            }
        });

        txtTitle.setText(items.get(position).getPlace());
        if(items.get(position).getLabel()!=null) {
            txtSubTitle.setText(items.get(position).getLabel());
        }
        imageView.setImageResource(items.get(position).getImageResourceId());
        return rowView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void remove(SelectedLocation object) {
        super.remove(object);
        notifyDataSetChanged();
    }
}
